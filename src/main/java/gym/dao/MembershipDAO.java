package gym.dao;

import gym.models.Membership;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MembershipDAO {

    private final Database db = Database.getInstance();

    public void create(Membership membership) {
        String sql = "INSERT INTO memberships (price, period, start_date, end_date, member_id) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = db.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDouble(1, membership.getPrice());
            stmt.setString(2, membership.getPeriod().name());
            stmt.setDate(3, java.sql.Date.valueOf(membership.getStartDate()));
            stmt.setDate(4, java.sql.Date.valueOf(membership.getEndDate()));
            stmt.setInt(5, membership.getMemberId());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    membership.setId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to create membership", e);
        }
    }

    public Membership findById(int id) {
        String sql = "SELECT membership_id, price, period, start_date, end_date, member_id "
                + "FROM memberships WHERE membership_id = ?";
        try (PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find membership by id", e);
        }
        return null;
    }

    public List<Membership> findAll() {
        String sql = "SELECT membership_id, price, period, start_date, end_date, member_id FROM memberships";
        List<Membership> memberships = new ArrayList<>();
        try (PreparedStatement stmt = db.getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                memberships.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find all memberships", e);
        }
        return memberships;
    }

    public List<Membership> findAllByMember(int memberId) {
        String sql = "SELECT membership_id, price, period, start_date, end_date, member_id "
                + "FROM memberships WHERE member_id = ?";
        List<Membership> memberships = new ArrayList<>();
        try (PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    memberships.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find memberships by member", e);
        }
        return memberships;
    }

    public void update(Membership membership) {
        String sql = "UPDATE memberships SET price = ?, period = ?, start_date = ?, end_date = ?, member_id = ? "
                + "WHERE membership_id = ?";
        try (PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
            stmt.setDouble(1, membership.getPrice());
            stmt.setString(2, membership.getPeriod().name());
            stmt.setDate(3, java.sql.Date.valueOf(membership.getStartDate()));
            stmt.setDate(4, java.sql.Date.valueOf(membership.getEndDate()));
            stmt.setInt(5, membership.getMemberId());
            stmt.setInt(6, membership.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to update membership", e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM memberships WHERE membership_id = ?";
        try (PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to delete membership", e);
        }
    }

    private Membership mapRow(ResultSet rs) throws SQLException {
        Membership membership = new Membership();
        membership.setId(rs.getInt("membership_id"));
        membership.setPrice(rs.getDouble("price"));
        membership.setPeriod(Membership.Period.valueOf(rs.getString("period")));
        membership.setStartDate(toLocalDate(rs.getDate("start_date")));
        membership.setEndDate(toLocalDate(rs.getDate("end_date")));
        membership.setMemberId(rs.getInt("member_id"));
        return membership;
    }

    private LocalDate toLocalDate(java.sql.Date date) {
        return date == null ? null : date.toLocalDate();
    }
}
