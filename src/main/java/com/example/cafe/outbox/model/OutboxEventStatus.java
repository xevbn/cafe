package com.example.cafe.outbox.model;

public enum OutboxEventStatus {
    PENDING {
        @Override
        public boolean canTransitTo() {
            return true;
        }
    },
    PROCESSED {
        @Override
        public boolean canTransitTo() {
            return false;
        }
    },
    FAILED {
        @Override
        public boolean canTransitTo() {
            return false;
        }
    };

    public abstract boolean canTransitTo();
}
