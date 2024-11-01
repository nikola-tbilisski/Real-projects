package com.kvantino.book.feedback;

import com.kvantino.book.book.Book;
import com.kvantino.book.book.BookRepository;
import com.kvantino.book.common.PageResponse;
import com.kvantino.book.exception.OperationNotPermittedException;
import com.kvantino.book.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final BookRepository bookRepository;
    private final FeedbackMapper feedbackMapper;
    private final FeedbackRepository feedbackRepository;

    public Long save(FeedbackRequest request, Authentication connectedUser) {

        Book book = bookRepository.findById(Math.toIntExact(request.bookId()))
                .orElseThrow(() -> new EntityNotFoundException("No book found with the ID :" + request.bookId()));

        if (book.isArchived() || !book.isShareable())
            throw new OperationNotPermittedException("You can't give a feedback for an archived or not shareable book");

        User user = ((User) connectedUser.getPrincipal());

        if (Objects.equals(book.getOwner().getId(), user.getId()))
            throw new OperationNotPermittedException("You can't feedback to your own book");

        Feedback feedback = feedbackMapper.toFeedback(request);

        return feedbackRepository.save(feedback).getId();
    }

    public PageResponse<FeedbackResponse> findAllFeedbacksByBook(Integer bookId, int page, int size, Authentication connectedUser) {

        Pageable pageable = PageRequest.of(page, size);
        User user = ((User) connectedUser.getPrincipal());
        Page<Feedback> feedbacks = feedbackRepository.findAllByBookId(bookId, pageable);
        List<FeedbackResponse> feedbackResponses = feedbacks.stream()
                .map(f -> feedbackMapper.toFeedbackResponse(f, user.getId()))
                .toList();

        return new PageResponse<>(
                feedbackResponses,
                feedbacks.getNumber(),
                feedbacks.getSize(),
                feedbacks.getTotalElements(),
                feedbacks.getTotalPages(),
                feedbacks.isFirst(),
                feedbacks.isLast()
        );
    }
}
