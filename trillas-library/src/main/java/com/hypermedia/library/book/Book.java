package com.hypermedia.library.book;
import com.hypermedia.library.common.BaseEntity;
import com.hypermedia.library.feedback.Feedback;
import com.hypermedia.library.history.BookTransactionHistory;
import com.hypermedia.library.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Book extends BaseEntity {

    private String title;
    private String authorName;
    private String isbn;
    private String synopsis;
    private String bookCover;
    private boolean archived;
    private boolean shareable;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "book")
    private List<Feedback> feedback;

    @OneToMany(mappedBy = "book")
    private List<BookTransactionHistory> histories;

    public double getRate() {
        if (feedback==null || feedback
                .isEmpty()){
            return 0.0;
        }
        var rate = feedback.stream()
                .mapToDouble(Feedback::getNote)
                .average()
                .orElse(0.0);
        return Math.round(rate*10.0)/10.0;

    }
}
