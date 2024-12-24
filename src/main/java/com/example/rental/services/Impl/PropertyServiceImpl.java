package com.example.rental.services.Impl;

import com.example.rental.dto.PropertyDto;
import com.example.rental.exceptions.AccessDeniedException;
import com.example.rental.exceptions.PropertyNotFoundException;
import com.example.rental.models.Agreement;
import com.example.rental.models.Property;
import com.example.rental.repositories.AgreementRepository;
import com.example.rental.repositories.PropertyRepository;
import com.example.rental.repositories.ReviewRepository;
import com.example.rental.services.PropertyService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@EnableCaching
public class PropertyServiceImpl implements PropertyService {
    private final PropertyRepository propertyRepository;
    private final AgreementRepository agreementRepository;
    private final ReviewRepository reviewRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public PropertyServiceImpl(PropertyRepository propertyRepository, AgreementRepository agreementRepository, ReviewRepository reviewRepository, ModelMapper modelMapper) {
        this.propertyRepository = propertyRepository;
        this.agreementRepository = agreementRepository;
        this.reviewRepository = reviewRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public void validatePropertyOwnershipByAgent(int propertyId, int agentId) {
        Property property = propertyRepository.findById(Property.class, propertyId);
        if (property == null) { throw new PropertyNotFoundException(); }
        if (property.getAgent().getId() != agentId) { throw new AccessDeniedException(); }
    }

    @Override
    @Cacheable("main")
    public Page<PropertyDto> choosingSort(String city, String sort, int page, int size, String search) {
        Page<PropertyDto> propertyPage;
        String sorting = sort != null ? sort : "no";
        if (search != null && !search.isBlank()) {
            propertyPage = searchProperty(search, page, size);
        }
        else if (city != null && !city.equals("no")) { propertyPage = findPropertyByCity(city, page, size); }
        else {
            propertyPage = switch (sorting) {
                case "profitable" -> findProfitableProperties(page, size);
                case "popular" -> findPopularProperties(page, size);
                default -> findPropertyByRating(page, size);
            };
        }
        return propertyPage;
    }

    @Override
    public Page<PropertyDto> searchProperty(String search, int page, int size) {
        Page<Property> properties = propertyRepository.searchProperty(search, page, size, "Available", false);
        return properties.map(property -> modelMapper.map(property, PropertyDto.class));
    }

    @Override
    public Page<PropertyDto> findPropertyByRating(int page, int size) {
        Page<Property> properties = propertyRepository.findPropertyByRating(page, size, "Available",false);
        return properties.map(property -> modelMapper.map(property, PropertyDto.class));
    }

    @Override
    public Page<PropertyDto> findPopularProperties(int page, int size) {
        List<PropertyDto> result = new ArrayList<>();
        Map<Integer, Integer> countArend = new HashMap<>();
        List<Property> propertyList = propertyRepository.findAvailableProperties("Available", false);

        for(Property property : propertyList) {
            countArend.put(property.getId(), agreementRepository.countAgreementsByPropertyId(property.getId(), false));
        }

        List<Map.Entry<Integer, Integer>> sortedEntries = countArend.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .toList();

        for (Map.Entry<Integer, Integer> entry : sortedEntries) {
            result.add(modelMapper.map(propertyRepository.findById(Property.class, entry.getKey()), PropertyDto.class));
        }

        List<PropertyDto> pages = result.subList((page - 1) * size, Math.min((page - 1) * size + size, result.size()));
        return new PageImpl<>(pages, PageRequest.of(page - 1, size), result.size());
    }

    @Override
    public Page<PropertyDto> findProfitableProperties(int page, int size) {
        List<PropertyDto> result = new ArrayList<>();
        Map<Integer, Double> profitMap = new HashMap<>();
        List<Property> propertyList = propertyRepository.findAvailableProperties("Available", false);

        for (Property property : propertyList) {
            double priceFactor = property.getSquare() / property.getPrice();
            double ratingFactor = property.getRating() / 5;
            double distanceFactor = 1 / (property.getDistanceToCenter() + 1);
            double profit = (priceFactor * 0.5) + (ratingFactor * 0.3) + (distanceFactor * 0.2);

            profitMap.put(property.getId(), profit);
        }

        List<Map.Entry<Integer, Double>> sortedEntries = profitMap.entrySet()
                .stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .toList();


        for (Map.Entry<Integer, Double> entry : sortedEntries) {
            result.add(modelMapper.map(propertyRepository.findById(Property.class, entry.getKey()), PropertyDto.class));
        }

        List<PropertyDto> pages = result.subList((page - 1) * size, Math.min((page - 1) * size + size, result.size()));
        return new PageImpl<>(pages, PageRequest.of(page - 1, size), result.size());
    }


    @Override
    public Page<PropertyDto> findPropertyByCity(String city, int page, int size) {
        Page<Property> propertyPage = propertyRepository.findAvailablePropertyByCity(page, size, city, "Available", false);
        return propertyPage.map(property -> modelMapper.map(property, PropertyDto.class));
    }

    @Override
    public List<PropertyDto> findAllPropertyByAgentId(int agentId) {
        return propertyRepository.findAllPropertyByAgentId(agentId, false).stream().map(p -> modelMapper.map(p, PropertyDto.class)).toList();
    }

    @Override
    public int countAvailablePropertyByAgentId(int agentId) {
        return propertyRepository.CountAvailablePropertyByAgentId(agentId, "Available", false);
    }

    @Override
    public List<PropertyDto> findPropertyByCurrentAgreementAndClientId(int ClientId) {
        return convertAgreementsToPropertyDto(agreementRepository.findCurrentAgreementByClientId(ClientId, LocalDate.now(), false));
    }

    @Override
    public List<PropertyDto> findPropertyByCompletedAgreementAndClientId(int ClientId) {
        return convertAgreementsToPropertyDto(agreementRepository.findCompletedAgreementByClientId(ClientId, LocalDate.now(), false));
    }

    private List<PropertyDto> convertAgreementsToPropertyDto(List<Agreement> agreements) {
        return agreements.stream().map(a -> modelMapper.map(a.getProperty(), PropertyDto.class)).toList();
    }

    @Override
    public void updatePropertyRating(Agreement agreement) {
        Property property = agreement.getProperty();

        double countReview = 0;
        double sumTotalRating = 0;

        List<Agreement> agreementList = agreementRepository.findAgreementsByReviewNotNullAndPropertyId(property.getId(), false);
        for (Agreement agreement1 : agreementList) {
            sumTotalRating += reviewRepository.findReviewByAgreementId(agreement1.getId()).getPropertyRating();
            countReview++;
        }

        double rating = Math.round((sumTotalRating / countReview) * 10.0) / 10.0;
        property.setRating(rating);
        propertyRepository.update(property);
    }

    @Override
    public Page<PropertyDto> findAll(int page, int size) {
        Page<Property> properties = propertyRepository.findAll(Property.class, page, size);

        List<PropertyDto> propertyDto = properties.getContent().stream()
                .map(property -> modelMapper.map(property, PropertyDto.class))
                .toList();

        return new PageImpl<>(propertyDto, properties.getPageable(), properties.getTotalElements());
    }

    @Override
    public PropertyDto findById(int id) {
        Property property = propertyRepository.findById(Property.class, id);
        if (property == null || property.getIsDeleted()) { throw new PropertyNotFoundException(); }
        return modelMapper.map(property, PropertyDto.class);
    }

    @Override
    @CacheEvict(cacheNames = "main", allEntries = true)
    public void create(PropertyDto propertyDto) {
        propertyRepository.create(modelMapper.map(propertyDto, Property.class));
    }

    @Override
    @CacheEvict(cacheNames = "main", allEntries = true)
    public void update(PropertyDto propertyDto, int id) {
        Property oldProperty = propertyRepository.findById(Property.class, id);
        Property pr = modelMapper.map(propertyDto, Property.class);
        pr.setId(id);
        pr.setRating(oldProperty.getRating());
        propertyRepository.update(pr);
    }

    @Override
    @CacheEvict(cacheNames = "main", allEntries = true)
    public void markDeleteById(int id) {
        Property property = propertyRepository.findById(Property.class, id);
        property.setIsDeleted(true);
        propertyRepository.update(property);
    }
}
