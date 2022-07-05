import React from 'react';
import { Droppable } from 'react-beautiful-dnd';
import { Issue } from 'Types/Issue';
import { BoardIssueTile } from '..';
import {
  StyledColumnContainer,
  StyledColumnHeader,
  StyledDroppableWrapper
} from './BoardColumn.styled';

interface Props {
  label: string;
  elements?: Issue.IssueEntity[];
}

export const BoardColumn = ({ label, elements }: Props): JSX.Element => {
  return (
    <StyledColumnContainer>
      <StyledColumnHeader>{label.replaceAll('_', ' ')}</StyledColumnHeader>
      <Droppable droppableId={label}>
        {(provided) => (
          <StyledDroppableWrapper {...provided.droppableProps} ref={provided.innerRef}>
            {elements &&
              elements.map((element: Issue.IssueEntity, index: number) => (
                <BoardIssueTile key={element.id} issue={element} index={index} />
              ))}
            {provided.placeholder}
          </StyledDroppableWrapper>
        )}
      </Droppable>
    </StyledColumnContainer>
  );
};