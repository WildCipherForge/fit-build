package gym.models;

import java.time.LocalDate;

public class Membership {

    public enum Period {
        MONTHLY,
        QUARTERLY,
        ANNUAL
    }

    private int id;
    private int memberId;
    private double price;
    private Period period;
    private LocalDate startDate;
    private LocalDate endDate;

    public Membership() {
    }

    public Membership(int id, int memberId, double price, Period period,
                       LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.memberId = memberId;
        this.price = price;
        this.period = period;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public boolean isActive() {
        LocalDate today = LocalDate.now();
        return !today.isBefore(startDate) && !today.isAfter(endDate);
    }
}
