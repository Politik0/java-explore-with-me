package ru.practicum.ewmservice.events.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.practicum.ewmservice.events.dto.CommentDto;
import ru.practicum.ewmservice.events.dto.InputCommentDto;
import ru.practicum.ewmservice.events.model.Comment;

import java.time.format.DateTimeFormatter;

@Component
public class CommentMapper {
    private final ModelMapper modelMapper;
    private final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public CommentMapper() {
        modelMapper = new ModelMapper();
    }

    public CommentDto convertToDto(Comment comment) {
        modelMapper.typeMap(Comment.class, CommentDto.class).addMappings(mapper ->
                mapper.map(src -> src.getAuthor().getName(), CommentDto::setAuthorName));
        CommentDto commentDto = modelMapper.map(comment, CommentDto.class);
        commentDto.setCreated(comment.getCreated().format(format));
        return commentDto;
    }

    public Comment convertFromDto(InputCommentDto commentDto) {
        return modelMapper.map(commentDto, Comment.class);
    }
}
