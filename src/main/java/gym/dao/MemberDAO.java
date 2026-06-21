package gym.dao;

import gym.models.Member;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MemberDAO {

    private final Database db = Database.getInstance();

    public void create(Member member) {
        String sql = "INSERT INTO members (first_name, last_name, birth_date, phone, email, registration_date) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = db.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, member.getFirstName());
            stmt.setString(2, member.getLastName());
            stmt.setDate(3, java.sql.Date.valueOf(member.getBirthDate()));
            stmt.setString(4, member.getPhone());
            stmt.setString(5, member.getEmail());
            stmt.setDate(6, java.sql.Date.valueOf(member.getRegistrationDate()));
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    member.setId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to create member", e);
        }
    }

    public Member findById(int id) {
        String sql = "SELECT member_id, first_name, last_name, birth_date, phone, email, registration_date "
                + "FROM members WHERE member_id = ?";
        try (PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find member by id", e);
        }
        return null;
    }

    public List<Member> findAll() {
        String sql = "SELECT member_id, first_name, last_name, birth_date, phone, email, registration_date "
                + "FROM members";
        List<Member> members = new ArrayList<>();
        try (PreparedStatement stmt = db.getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                members.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find all members", e);
        }
        return members;
    }

    public void update(Member member) {
        String sql = "UPDATE members SET first_name = ?, last_name = ?, birth_date = ?, phone = ?, email = ? "
                + "WHERE member_id = ?";
        try (PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
            stmt.setString(1, member.getFirstName());
            stmt.setString(2, member.getLastName());
            stmt.setDate(3, java.sql.Date.valueOf(member.getBirthDate()));
            stmt.setString(4, member.getPhone());
            stmt.setString(5, member.getEmail());
            stmt.setInt(6, member.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to update member", e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM members WHERE member_id = ?";
        try (PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to delete member", e);
        }
    }

    private Member mapRow(ResultSet rs) throws SQLException {
        Member member = new Member();
        member.setId(rs.getInt("member_id"));
        member.setFirstName(rs.getString("first_name"));
        member.setLastName(rs.getString("last_name"));
        member.setBirthDate(toLocalDate(rs.getDate("birth_date")));
        member.setPhone(rs.getString("phone"));
        member.setEmail(rs.getString("email"));
        member.setRegistrationDate(toLocalDate(rs.getDate("registration_date")));
        return member;
    }

    private LocalDate toLocalDate(java.sql.Date date) {
        return date == null ? null : date.toLocalDate();
    }
}
