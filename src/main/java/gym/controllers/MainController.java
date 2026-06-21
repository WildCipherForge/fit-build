package gym.controllers;

import gym.dao.MemberDAO;
import gym.dao.MembershipDAO;
import gym.models.Member;
import gym.models.Membership;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;

public class MainController {

    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private DatePicker birthDatePicker;
    @FXML
    private DatePicker registrationDatePicker;

    @FXML
    private TableView<Member> memberTable;
    @FXML
    private TableColumn<Member, Integer> idColumn;
    @FXML
    private TableColumn<Member, String> firstNameColumn;
    @FXML
    private TableColumn<Member, String> lastNameColumn;
    @FXML
    private TableColumn<Member, String> emailColumn;
    @FXML
    private TableColumn<Member, String> phoneColumn;
    @FXML
    private TableColumn<Member, LocalDate> birthDateColumn;
    @FXML
    private TableColumn<Member, LocalDate> registrationDateColumn;

    @FXML
    private TextField priceField;
    @FXML
    private ComboBox<Membership.Period> periodComboBox;
    @FXML
    private DatePicker startDatePicker;

    @FXML
    private TableView<Membership> membershipTable;
    @FXML
    private TableColumn<Membership, Integer> membershipIdColumn;
    @FXML
    private TableColumn<Membership, Double> membershipPriceColumn;
    @FXML
    private TableColumn<Membership, Membership.Period> membershipPeriodColumn;
    @FXML
    private TableColumn<Membership, LocalDate> membershipStartDateColumn;
    @FXML
    private TableColumn<Membership, LocalDate> membershipEndDateColumn;
    @FXML
    private TableColumn<Membership, Boolean> membershipActiveColumn;

    @FXML
    private Label totalMembersLabel;
    @FXML
    private Label totalMembershipsLabel;
    @FXML
    private Label activeMembershipsLabel;
    @FXML
    private Label totalRevenueLabel;
    @FXML
    private Label monthlyCountLabel;
    @FXML
    private Label quarterlyCountLabel;
    @FXML
    private Label annualCountLabel;

    private final MemberDAO memberDAO = new MemberDAO();
    private final MembershipDAO membershipDAO = new MembershipDAO();
    private final ObservableList<Member> members = FXCollections.observableArrayList();
    private final ObservableList<Membership> memberships = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        birthDateColumn.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        registrationDateColumn.setCellValueFactory(new PropertyValueFactory<>("registrationDate"));

        memberTable.setItems(members);
        memberTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                populateForm(newVal);
                refreshMemberships(newVal.getId());
            } else {
                memberships.clear();
            }
        });

        membershipIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        membershipPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        membershipPeriodColumn.setCellValueFactory(new PropertyValueFactory<>("period"));
        membershipStartDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        membershipEndDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        membershipActiveColumn.setCellValueFactory(new PropertyValueFactory<>("active"));
        membershipTable.setItems(memberships);

        periodComboBox.getItems().setAll(Membership.Period.values());

        refreshMembers();
    }

    @FXML
    private void handleAdd() {
        try {
            Member member = new Member();
            member.setFirstName(firstNameField.getText());
            member.setLastName(lastNameField.getText());
            member.setEmail(emailField.getText());
            member.setPhone(phoneField.getText());
            member.setBirthDate(birthDatePicker.getValue());
            member.setRegistrationDate(registrationDatePicker.getValue());

            memberDAO.create(member);
            refreshMembers();
            handleClear();
        } catch (Exception e) {
            showError("Failed to add member: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        Member selected = memberTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select a member to update.");
            return;
        }
        try {
            selected.setFirstName(firstNameField.getText());
            selected.setLastName(lastNameField.getText());
            selected.setEmail(emailField.getText());
            selected.setPhone(phoneField.getText());
            selected.setBirthDate(birthDatePicker.getValue());

            memberDAO.update(selected);
            refreshMembers();
            handleClear();
        } catch (Exception e) {
            showError("Failed to update member: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        Member selected = memberTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select a member to delete.");
            return;
        }
        try {
            memberDAO.delete(selected.getId());
            refreshMembers();
            handleClear();
        } catch (Exception e) {
            showError("Failed to delete member: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddMembership() {
        Member selectedMember = memberTable.getSelectionModel().getSelectedItem();
        if (selectedMember == null) {
            showError("Select a member to add a membership for.");
            return;
        }
        try {
            Membership.Period period = periodComboBox.getValue();
            LocalDate startDate = startDatePicker.getValue();
            if (period == null || startDate == null) {
                showError("Period and start date are required.");
                return;
            }

            Membership membership = new Membership();
            membership.setMemberId(selectedMember.getId());
            membership.setPrice(Double.parseDouble(priceField.getText()));
            membership.setPeriod(period);
            membership.setStartDate(startDate);
            membership.setEndDate(computeEndDate(startDate, period));

            membershipDAO.create(membership);
            refreshMemberships(selectedMember.getId());
        } catch (NumberFormatException e) {
            showError("Price must be a number.");
        } catch (Exception e) {
            showError("Failed to add membership: " + e.getMessage());
        }
    }

    @FXML
    private void handleRenewMembership() {
        Membership selected = membershipTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select a membership to renew.");
            return;
        }
        try {
            LocalDate renewalStart = selected.getEndDate().isBefore(LocalDate.now())
                    ? LocalDate.now()
                    : selected.getEndDate();
            selected.setStartDate(renewalStart);
            selected.setEndDate(computeEndDate(renewalStart, selected.getPeriod()));

            membershipDAO.update(selected);
            refreshMemberships(selected.getMemberId());
        } catch (Exception e) {
            showError("Failed to renew membership: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancelMembership() {
        Membership selected = membershipTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select a membership to cancel.");
            return;
        }
        try {
            membershipDAO.delete(selected.getId());
            refreshMemberships(selected.getMemberId());
        } catch (Exception e) {
            showError("Failed to cancel membership: " + e.getMessage());
        }
    }

    private LocalDate computeEndDate(LocalDate startDate, Membership.Period period) {
        return switch (period) {
            case MONTHLY -> startDate.plusMonths(1);
            case QUARTERLY -> startDate.plusMonths(3);
            case ANNUAL -> startDate.plusYears(1);
        };
    }

    private void refreshMemberships(int memberId) {
        memberships.setAll(membershipDAO.findAllByMember(memberId));
    }

    @FXML
    private void handleStatisticsTabSelected() {
        refreshStatistics();
    }

    @FXML
    private void handleRefreshStatistics() {
        refreshStatistics();
    }

    private void refreshStatistics() {
        try {
            List<Member> allMembers = memberDAO.findAll();
            List<Membership> allMemberships = membershipDAO.findAll();

            long activeCount = allMemberships.stream().filter(Membership::isActive).count();
            double totalRevenue = allMemberships.stream().mapToDouble(Membership::getPrice).sum();
            long monthlyCount = allMemberships.stream()
                    .filter(m -> m.getPeriod() == Membership.Period.MONTHLY).count();
            long quarterlyCount = allMemberships.stream()
                    .filter(m -> m.getPeriod() == Membership.Period.QUARTERLY).count();
            long annualCount = allMemberships.stream()
                    .filter(m -> m.getPeriod() == Membership.Period.ANNUAL).count();

            totalMembersLabel.setText("Total members: " + allMembers.size());
            totalMembershipsLabel.setText("Total memberships: " + allMemberships.size());
            activeMembershipsLabel.setText("Active memberships: " + activeCount);
            totalRevenueLabel.setText(String.format("Total revenue: %.2f", totalRevenue));
            monthlyCountLabel.setText("Monthly memberships: " + monthlyCount);
            quarterlyCountLabel.setText("Quarterly memberships: " + quarterlyCount);
            annualCountLabel.setText("Annual memberships: " + annualCount);
        } catch (Exception e) {
            showError("Failed to load statistics: " + e.getMessage());
        }
    }

    @FXML
    private void handleExportData(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Export data");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));
        chooser.setInitialFileName("gym_export.csv");

        Node source = (Node) event.getSource();
        File file = chooser.showSaveDialog(source.getScene().getWindow());
        if (file == null) {
            return;
        }

        try {
            exportToCsv(file);
        } catch (IOException e) {
            showError("Failed to export data: " + e.getMessage());
        }
    }

    private void exportToCsv(File file) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath())) {
            writer.write("member_id,first_name,last_name,email,phone,birth_date,registration_date,"
                    + "membership_id,price,period,start_date,end_date,active");
            writer.newLine();

            for (Member member : memberDAO.findAll()) {
                List<Membership> memberMemberships = membershipDAO.findAllByMember(member.getId());
                if (memberMemberships.isEmpty()) {
                    writer.write(memberRow(member) + ",,,,,");
                    writer.newLine();
                } else {
                    for (Membership membership : memberMemberships) {
                        writer.write(memberRow(member) + "," + membershipRow(membership));
                        writer.newLine();
                    }
                }
            }
        }
    }

    private String memberRow(Member member) {
        return member.getId() + ","
                + csvEscape(member.getFirstName()) + ","
                + csvEscape(member.getLastName()) + ","
                + csvEscape(member.getEmail()) + ","
                + csvEscape(member.getPhone()) + ","
                + member.getBirthDate() + ","
                + member.getRegistrationDate();
    }

    private String membershipRow(Membership membership) {
        return membership.getId() + ","
                + membership.getPrice() + ","
                + membership.getPeriod() + ","
                + membership.getStartDate() + ","
                + membership.getEndDate() + ","
                + membership.isActive();
    }

    private String csvEscape(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    @FXML
    private void handleClear() {
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        phoneField.clear();
        birthDatePicker.setValue(null);
        registrationDatePicker.setValue(null);
        memberTable.getSelectionModel().clearSelection();
        priceField.clear();
        periodComboBox.setValue(null);
        startDatePicker.setValue(null);
    }

    private void populateForm(Member member) {
        firstNameField.setText(member.getFirstName());
        lastNameField.setText(member.getLastName());
        emailField.setText(member.getEmail());
        phoneField.setText(member.getPhone());
        birthDatePicker.setValue(member.getBirthDate());
        registrationDatePicker.setValue(member.getRegistrationDate());
    }

    private void refreshMembers() {
        members.setAll(memberDAO.findAll());
    }

    private void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR, message);
        alert.showAndWait();
    }
}
