package com.neueda.etiqet.orderbook.etiqetorderbook.entity;

import org.apache.commons.lang3.StringUtils;

import static com.neueda.etiqet.orderbook.etiqetorderbook.utils.Constants.Type;

/**
 * Shows row with data from buy and sell side orders match
 * Also shows cancelled orders
 */
public class Action {
    public Type type;
    private String buyID;
    private String sellID;
    private String buyClientID;
    private String sellClientID;
    private String timeInForceSell;
    private String timeInForceBuy;
    private String time;
    private Double buySize;
    private Double sellSize;
    private Double leaveQty;
    private Double agreedPrice;

    public Action(ActionBuilder actionBuilder) {
        this();
        this.type = actionBuilder.type;
        this.buyID = actionBuilder.buyID;
        this.buyClientID = actionBuilder.buyClientID;
        this.sellClientID = actionBuilder.sellClientID;
        this.timeInForceBuy = actionBuilder.timeInForceBuy;
        this.timeInForceSell = actionBuilder.timeInForceSell;
        this.sellID = actionBuilder.sellID;
        this.time = actionBuilder.time;
        this.buySize = actionBuilder.buySize;
        this.sellSize = actionBuilder.sellSize;
        this.leaveQty = actionBuilder.leaveQty;
        this.agreedPrice = actionBuilder.agreedPrice;
    }

    public Action() {
        this.type = Type.CANCELED;
        this.buyID = StringUtils.EMPTY;
        this.buyClientID = StringUtils.EMPTY;
        this.sellClientID = StringUtils.EMPTY;
        this.timeInForceBuy = StringUtils.EMPTY;
        this.timeInForceSell = StringUtils.EMPTY;
        this.sellID = StringUtils.EMPTY;
        this.time = StringUtils.EMPTY;
        this.buySize = 0d;
        this.sellSize = 0d;
        this.leaveQty = 0d;
        this.agreedPrice = 0d;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getBuyID() {
        return buyID;
    }

    public void setBuyID(String buyID) {
        this.buyID = buyID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Double getBuySize() {
        return buySize;
    }

    public void setBuySize(Double buySize) {
        this.buySize = buySize;
    }

    public Double getLeaveQty() {
        return leaveQty;
    }

    public void setLeaveQty(Double leaveQty) {
        this.leaveQty = leaveQty;
    }

    public Double getSellSize() {
        return sellSize;
    }

    public void setSellSize(Double sellSize) {
        this.sellSize = sellSize;
    }

    public Double getAgreedPrice() {
        return agreedPrice;
    }

    public void setAgreedPrice(Double agreedPrice) {
        this.agreedPrice = agreedPrice;
    }

    public String getSellID() {
        return sellID;
    }

    public void setSellID(String sellID) {
        this.sellID = sellID;
    }

    public String getBuyClientID() {
        return buyClientID;
    }

    public void setBuyClientID(String buyClientID) {
        this.buyClientID = buyClientID;
    }

    public String getSellClientID() {
        return sellClientID;
    }

    public void setSellClientID(String sellClientID) {
        this.sellClientID = sellClientID;
    }

    public String getTimeInForceSell() {
        return timeInForceSell;
    }

    public void setTimeInForceSell(String timeInForceSell) {
        this.timeInForceSell = timeInForceSell;
    }

    public String getTimeInForceBuy() {
        return timeInForceBuy;
    }

    public void setTimeInForceBuy(String timeInForceBuy) {
        this.timeInForceBuy = timeInForceBuy;
    }

    @Override
    public String toString() {
        return "Action{" +
            "type=" + type +
            ", buyID='" + buyID + '\'' +
            ", sellID='" + sellID + '\'' +
            ", buyClientID='" + buyClientID + '\'' +
            ", sellClientID='" + sellClientID + '\'' +
            ", time='" + time + '\'' +
            ", buySize=" + buySize +
            ", sellSize=" + sellSize +
            ", leaveQty=" + leaveQty +
            ", agreedPrice=" + agreedPrice +
            '}';
    }

    public static class ActionBuilder {
        public Type type;
        private String buyID;
        private String sellID;
        private String buyClientID;
        private String sellClientID;
        private String timeInForceSell;
        private String timeInForceBuy;
        private String time;
        private Double buySize;
        private Double sellSize;
        private Double leaveQty;
        private Double agreedPrice;

        public ActionBuilder() {
            this.type = Type.CANCELED;
            this.buyID = StringUtils.EMPTY;
            this.sellID = StringUtils.EMPTY;
            this.buyClientID = StringUtils.EMPTY;
            this.sellClientID = StringUtils.EMPTY;
            this.timeInForceSell = StringUtils.EMPTY;
            this.timeInForceBuy = StringUtils.EMPTY;
            this.time = StringUtils.EMPTY;
            this.buySize = 0d;
            this.sellSize = 0d;
            this.leaveQty = 0d;
            this.agreedPrice = 0d;
        }

        public ActionBuilder type(Type type) {
            synchronized (this.type) {
                this.type = type;
                return this;
            }
        }

        public ActionBuilder buyID(String buyID) {
            synchronized (this.buyID) {
                this.buyID = buyID;
                return this;
            }
        }

        public ActionBuilder sellID(String sellID) {
            synchronized (this.sellID) {
                this.sellID = sellID;
                return this;
            }
        }

        public ActionBuilder buyClientID(String buyClientID) {
            synchronized (this.buyClientID) {
                this.buyClientID = buyClientID;
                return this;
            }
        }

        public ActionBuilder sellClientID(String sellClientID) {
            synchronized (this.sellClientID) {
                this.sellClientID = sellClientID;
                return this;
            }
        }

        public ActionBuilder timeInForceSell(String timeInForceSell) {
            synchronized (this.timeInForceSell) {
                this.timeInForceSell = timeInForceSell;
                return this;
            }
        }

        public ActionBuilder timeInForceBuy(String timeInForceBuy) {
            synchronized (this.timeInForceBuy) {
                this.timeInForceBuy = timeInForceBuy;
                return this;
            }
        }

        public ActionBuilder time(String time) {
            synchronized (this.time) {
                this.time = time;
                return this;
            }

        }

        public ActionBuilder buySize(Double buySize) {
            synchronized (this.buySize) {
                this.buySize = buySize;
                return this;
            }

        }

        public ActionBuilder sellSize(Double sellSize) {
            synchronized (this.sellSize) {
                this.sellSize = sellSize;
                return this;
            }

        }

        public ActionBuilder leaveQty(Double leaveQty) {
            synchronized (this.leaveQty) {
                this.leaveQty = leaveQty;
                return this;
            }

        }

        public ActionBuilder agreedPrice(Double agreedPrice) {
            synchronized (this.agreedPrice) {
                this.agreedPrice = agreedPrice;
                return this;
            }

        }

        public Action build() {
            return new Action(this);
        }

    }
}
