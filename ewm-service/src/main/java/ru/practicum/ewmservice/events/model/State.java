package ru.practicum.ewmservice.events.model;

public enum State {
    PENDING,

    PUBLISHED,

    CANCELED,

    REJECTED,

    SEND_TO_REVIEW,

    CANCEL_REVIEW,

    PUBLISH_EVENT,

    REJECT_EVENT
}
