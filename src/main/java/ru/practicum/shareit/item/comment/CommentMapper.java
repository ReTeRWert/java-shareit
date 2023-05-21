package ru.practicum.shareit.item.comment;

public class CommentMapper {
    public static Comment toComment(CommentDto commentDto) {
        return new Comment(
                commentDto.getText(),
                commentDto.getCreated()
        );
    }

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreationDate()
        );
    }
}
