package com.thistroll.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Domain object representing some text spoken to the troll and possible appropriate responses
 *
 * The id of the Speech object is a hash of the speech text
 *
 * Created by MVW on 10/6/2017.
 */
public class Speech extends AbstractPersistentObject {

    public static final String TEXT_PROPERTY = "text";
    public static final String RESPONSES_PROPERTY = "responses";

    /**
     * The words spoken to the troll - this is immutable and can never be updated. The ID is calculated from this field.
     */
    private final String text;

    /**
     * A list of possible responses
     */
    private final List<String> responses;

    /**
     * No-arg constructor required by Jackson
     */
    private Speech() {
        super();
        this.text = null;
        this.responses = null;
    }

    private Speech(Builder builder) {
        super(builder);
        this.text = builder.text;
        this.responses = builder.responses;
    }

    public String getText() {
        return text;
    }

    public List<String> getResponses() {
        return responses;
    }

    @JsonIgnore
    public String getRandomResponse() {
        if (responses.size() == 0) {
            return null;
        } else if (responses.size() == 1) {
            return responses.get(0);
        }

        int index = (int)(Math.random() * responses.size());
        return responses.get(index);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Speech)) return false;
        if (!super.equals(o)) return false;
        Speech speech = (Speech) o;
        return speech.canEqual(this) &&
                Objects.equals(text, speech.text) &&
                Objects.equals(responses, speech.responses);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(super.hashCode(), text, responses);
    }

    public boolean canEqual(Object o) {
        return o instanceof Speech;
    }

    public static final class Builder extends AbstractPersistentObject.Builder<Builder> {
        private String text;
        private List<String> responses = Collections.emptyList();

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder responses(List<String> responses) {
            this.responses = responses;
            return this;
        }

        public Speech build() {
            return new Speech(this);
        }
    }
}
