package ru.practicum.shareit.item.comment;

public class CommentMapper {
    public static Comment dtoToComment(CommentDto commentDto) {
        return new Comment(
                commentDto.getText(),
                commentDto.getCreated()
        );
    }

    public static CommentDto commentToDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreationDate()
        );
    }
}
