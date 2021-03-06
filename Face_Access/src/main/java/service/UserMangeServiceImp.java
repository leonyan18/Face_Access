package service;


import dao.AuthorityDao;
import dao.HouseDao;
import dao.UserDao;
import entity.AuthorityEntity;
import entity.HouseEntity;
import entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import util.EncryptInfo;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserMangeServiceImp implements UserMangeService {

    private AuthorityDao authorityDao;
    private UserDao userDao;
    private HouseDao houseDao;

    @Autowired
    public void setHouseDao(HouseDao houseDao) {
        this.houseDao = houseDao;
    }

    @Autowired
    public void setAuthorityDao(AuthorityDao authorityDao) {
        this.authorityDao = authorityDao;
    }

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }


    @Override
    public List<HouseEntity> getHousesByOwner(int userID) {
        return houseDao.getHouses(userID);
    }

    @Override
    public List<AuthorityEntity> getAuthoritiesByHouse(int houseID) {
        return authorityDao.getAuthoritiesOfHouse(houseID);
    }

    @Override
    public List<AuthorityEntity> getAuthoritiesByOwner(int userID) {
        List<HouseEntity> houseEntities = getHousesByOwner(userID);
        List<AuthorityEntity> authorityEntities = new ArrayList<AuthorityEntity>();
        for (HouseEntity houseEntity : houseEntities) {
            authorityEntities.addAll(getAuthoritiesByHouse(houseEntity.getHouseId()));
        }
        return authorityEntities;
    }

    @Override
    public void updateEndDate(int authorityID, Date endDate) {
        authorityDao.updateEndDate(authorityID, endDate);
    }

    @Override
    public int updateRemark(int authorityID, String remark) {
        return authorityDao.updateRemark(authorityID, remark);
    }

    @Override
    public int updatePassword(int userId, String password, String oldPassword) {
        UserEntity entity = userDao.getUserById(userId);
        password = EncryptInfo.MD5(password);
        oldPassword = EncryptInfo.MD5(oldPassword);
        if (entity.getUserPassword().equals(oldPassword)) {
            entity.setUserPassword(password);
            userDao.updateUser(entity);
            return 1;
        }
        return 0;
    }

    @Override
    public int updateTelephone(int userId, String telephone) {
        UserEntity entity = new UserEntity();
        entity.setUserTelephone(telephone);
        entity.setUserId(userId);
        userDao.updateUser(entity);
        return 0;
    }

    @Override
    public int updateUserFace(int userId, byte[] face) {
        UserEntity entity = new UserEntity();
        entity.setUserFace(face);
        entity.setUserId(userId);
        userDao.updateUser(entity);
        return 0;
    }

    @Override
    public void addAuthority(int houseID, int userID, Date startDate, Date endDate, String remark) {
        AuthorityEntity entity = new AuthorityEntity(userID, houseID);
        entity.setEndDate(endDate);
        entity.setStartDate(startDate);
        entity.setRemark(remark);
        authorityDao.addAuthority(entity);
    }

    @Override
    public void deleteAuthority(int authorityID) {
        authorityDao.deleteAuthority(authorityID);
    }

    @Override
    public UserEntity getUserEntity(int userID) {
        return userDao.getUserById(userID);
    }

    @Override
    public UserEntity getUserByUsername(String username) {
        return userDao.getUserByName(username);
    }

    @Override
    public List<UserEntity> getUserList() {
        return userDao.getUserList();
    }


}
