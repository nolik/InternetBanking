package com.NovikIgor.dao.impl;

import com.NovikIgor.dao.CardManagementDAO;
import com.NovikIgor.dao.pool.ConnectionPool;
import com.NovikIgor.dto.Card;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Class implement Card DAO model for takes cards information from DB.
 * <p>
 * Created by nolik on 25.10.16.
 */
public class CardManagmentDAOimpl implements CardManagementDAO {

    private static final String SQL_SELECT_ALL_CARDS = "SELECT * FROM InternetBanking.Cards";
    private static final String SQL_SELECT_ALL_CARDS_MAIN_INFO = "SELECT cardNumber,summ,Users_login,currencyName FROM InternetBanking.Cards INNER JOIN Currencies ON Currencies_currencyName=Currencies.currencyName";
    private static final String SQL_SELECT_CARDS_BY_LOGIN = "SELECT cardNumber,summ,Users_login,currencyName FROM InternetBanking.Cards INNER JOIN Currencies ON Currencies_currencyName=Currencies.currencyName WHERE Cards.Users_login=?";
    private static final String SQL_SELECT_CARDS_BY_CARDID ="SELECT cardNumber,summ,Users_login,currencyName FROM InternetBanking.Cards INNER JOIN Currencies ON Currencies_currencyName=Currencies.currencyName WHERE Cards.cardNumber=?";
    private static final String SQL_CHECK_CARD_IN_DB ="SELECT * FROM InternetBanking.Cards WHERE cardNumber=?";
    private static final String SQL_EDIT_CART_SUM = "UPDATE `InternetBanking`.`Cards` SET `summ`=? WHERE `cardNumber`=?";

    private static final Logger logger = Logger.getLogger(CardManagmentDAOimpl.class);


    public List<Card> getAllCards() {
        logger.info("Try to get All Cards from DB");
        Connection conn = null;
        PreparedStatement state = null;
        //TODO: change here to ConcurentLinkedQueue<Card> implementation?
        List<Card> allCards = new ArrayList<Card>();

        try {
            conn = ConnectionPool.getConnection();
            state = conn.prepareStatement(SQL_SELECT_ALL_CARDS_MAIN_INFO);
            ResultSet resultSet = state.executeQuery();

            while (resultSet.next()) {
                Card card = new Card();
                card.setCardNumber(resultSet.getInt(1));
                card.setSum(resultSet.getBigDecimal(2));
                card.setUser(resultSet.getString(3));
                card.setCurrency(resultSet.getString(4));
                allCards.add(card);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            ConnectionPool.close(state);
            ConnectionPool.close(conn);
        }

        return allCards;
    }

    public List<Card> getCardsByLogin(String login) {
        logger.info("Try to get Card by login=" + login + " Cards from DB");
        Connection conn = null;
        PreparedStatement state = null;
        List<Card> allCards = new ArrayList<Card>();

        try {
            conn = ConnectionPool.getConnection();
            state = conn.prepareStatement(SQL_SELECT_CARDS_BY_LOGIN);
            state.setString(1, login);
            ResultSet resultSet = state.executeQuery();

            while (resultSet.next()) {
                Card card = new Card();
                card.setCardNumber(resultSet.getInt(1));
                card.setSum(resultSet.getBigDecimal(2));
                card.setUser(resultSet.getString(3));
                card.setCurrency(resultSet.getString(4));
                allCards.add(card);
            }
        } catch (SQLException e) {
            logger.error("sql crashed during the selection by user", e);
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            ConnectionPool.close(state);
            ConnectionPool.close(conn);
        }

        return allCards;
    }

    public Card getCardByCardID(int userID) {
        Connection conn = null;
        PreparedStatement state = null;
        Card card = null;

        try {
            conn=ConnectionPool.getConnection();
            state=conn.prepareStatement(SQL_SELECT_CARDS_BY_CARDID);
            state.setInt(1,userID);
            ResultSet resultSet = state.executeQuery();

            while (resultSet.next()) {
                card = new Card();
                card.setCardNumber(resultSet.getInt(1));
                card.setSum(resultSet.getBigDecimal(2));
                card.setUser(resultSet.getString(3));
                card.setCurrency(resultSet.getString(4));
                return card;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return card;
    }

    public boolean checkCartID(int cartID) {
        Connection conn = null;
        PreparedStatement state = null;
        boolean haveCart = false;

        try {
            conn = ConnectionPool.getConnection();
            state = conn.prepareStatement(SQL_CHECK_CARD_IN_DB, Statement.RETURN_GENERATED_KEYS);
            state.setInt(1, cartID);
            ResultSet rs = state.executeQuery();
            haveCart = rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("SQLExp where checkCart", e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return haveCart;
    }

    public boolean editCartSum(Card card, Connection connection) throws SQLException {
        PreparedStatement state = null;
        state = connection.prepareStatement(SQL_EDIT_CART_SUM);
        state.setBigDecimal(1,card.getSum());
        state.setInt(2,card.getCardNumber());
        return state.execute();
    }
}
