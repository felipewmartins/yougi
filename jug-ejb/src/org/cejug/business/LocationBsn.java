package org.cejug.business;

import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.cejug.entity.City;
import org.cejug.entity.Country;
import org.cejug.entity.Province;
import org.cejug.util.EntitySupport;

/**
 * Manages data of countries, states or provinces and cities because these
 * three entities are strongly related and because they are too simple to
 * have an exclusive business class.
 * @author Hildeberto Mendonca
 */
@Stateless
@LocalBean
public class LocationBsn {
    @PersistenceContext
    private EntityManager em;

    public Country findCountry(String acronym) {
        if(acronym != null)
            return em.find(Country.class, acronym);
        else
            return null;
    }

    @SuppressWarnings("unchecked")
    public List<Country> findCountries() {
        return em.createQuery("select c from Country c order by c.name asc")
                 .getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Country> findAssociatedCountries() {
        return em.createQuery("select distinct p.country from Province p order by p.country")
                 .getResultList();
    }

    public Province findProvince(String id) {
        return em.find(Province.class, id);
    }

    @SuppressWarnings("unchecked")
    public List<Province> findProvinces() {
        return em.createQuery("select p from Province p order by p.country.name, p.name asc")
                 .getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Province> findProvinces(Country country) {
        return em.createQuery("select p from Province p where p.country = :country order by p.name asc")
                 .setParameter("country", country)
                 .getResultList();
    }

    public City findCity(String id) {
        return em.find(City.class, id);
    }

    @SuppressWarnings("unchecked")
    public List<City> findCities() {
        return em.createQuery("select c from City c order by c.country.name, c.name asc")
                 .getResultList();
    }
    
    @SuppressWarnings("unchecked")
    public List<City> findValidatedCities() {
        return em.createQuery("select c from City c where c.valid = :valid")
        		 .setParameter("valid", true)
                 .getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<City> findCities(Country country, Boolean includingInvalids) {
        if(includingInvalids)
            return em.createQuery("select c from City c where c.country = :country order by c.name asc")
                 .setParameter("country", country)
                 .getResultList();
        else
            return em.createQuery("select c from City c where c.country = :country and c.valid = :valid order by c.name asc")
                 .setParameter("country", country)
                 .setParameter("valid", Boolean.TRUE)
                 .getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<City> findCities(Province province, Boolean includingInvalids) {
        if(includingInvalids)
            return em.createQuery("select c from City c where c.province = :province order by c.name asc")
                 .setParameter("province", province)
                 .getResultList();
        else
            return em.createQuery("select c from City c where c.province = :province and c.valid = :valid order by c.name asc")
                 .setParameter("province", province)
                 .setParameter("valid", Boolean.TRUE)
                 .getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<City> findCitiesStartingWith(String initials) {
        return em.createQuery("select c from City c where c.name like '"+ initials +"%' order by c.name").getResultList();
    }

    @SuppressWarnings("unchecked")
    public City findCityByName(String name) {
        List<City> candidates = em.createQuery("select c from City c where c.name = :name")
                 .setParameter("name", name)
                 .getResultList();
        if(candidates != null && candidates.size() == 1) {
            return candidates.get(0);
        }
        else
            return null;
    }

    public void saveCountry(Country country) {
        Country existing = em.find(Country.class, country.getAcronym());
        if(existing == null)
            em.persist(country);
        else
            em.merge(country);
    }

    public void removeCountry(String id) {
        Country country = em.find(Country.class, id);
        if(country != null)
            em.remove(country);
    }

    public void saveProvince(Province province) {
        if(province.getId() == null || province.getId().isEmpty()) {
            province.setId(EntitySupport.generateEntityId());
            em.persist(province);
        }
        else {
            em.merge(province);
        }
    }

    public void removeProvince(String id) {
        Province province = em.find(Province.class, id);
        if(province != null)
            em.remove(province);
    }

    public void saveCity(City city) {
        if(city.getId() == null || city.getId().isEmpty()) {
            city.setId(EntitySupport.generateEntityId());
            em.persist(city);
        }
        else {
            em.merge(city);
        }
    }

    public void removeCity(String id) {
        City city = em.find(City.class, id);
        if(city != null)
            em.remove(city);
    }
}