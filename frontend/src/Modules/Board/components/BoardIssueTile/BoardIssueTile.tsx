import React from 'react';
import { Draggable } from 'react-beautiful-dnd';
import { CurrentProjectModel } from 'Models/CurrentProjectModel';
import { Issue } from 'Types/Issue';
import { IssueTypeIcon } from 'Shared/IssueTypeIcon';
import { StoryPointBadge } from 'Shared/StoryPointBadge';
import { PriorityBadge } from 'Shared/PriorityBadge';
import { isDefined } from 'Utils/isDefined';
import {
  StyledInformationContainer,
  StyledIssueCode,
  StyledIssueTitle,
  StyledIssueTitleContainer,
  StyledParentIssueRow,
  StyledTile
} from './BoardIssueTile.styled';

interface Props {
  issue: Issue.IssueEntity;
  index: number;
  isSubtask: boolean;
  parent?: Issue.IssueEntity;
  isDragDisabled: boolean;
  handleViewingIssue: (id: Id) => VoidFunctionNoArgs;
}

export const BoardIssueTile = ({
  issue,
  index,
  isSubtask,
  parent,
  isDragDisabled,
  handleViewingIssue
}: Props): JSX.Element => {
  const currentProject = CurrentProjectModel.currentProjectValue;

  return (
    <Draggable draggableId={issue.id} index={index} isDragDisabled={isDragDisabled}>
      {(provided, snapshot) => (
        <StyledTile
          ref={provided.innerRef}
          isSubtask={isSubtask}
          isParentInSameColumn={!isDefined(parent)}
          snapshot={snapshot}
          onClick={handleViewingIssue(issue.id)}
          {...provided.draggableProps}
          {...provided.dragHandleProps}
        >
          {parent && (
            <StyledParentIssueRow>{`${currentProject.code}-${parent.key} ${parent.title}`}</StyledParentIssueRow>
          )}
          <StyledIssueTitleContainer>
            <StyledIssueTitle>{issue.title}</StyledIssueTitle>
            <StyledIssueCode>{`${currentProject.code}-${issue.key}`}</StyledIssueCode>
          </StyledIssueTitleContainer>
          <StyledInformationContainer>
            <IssueTypeIcon type={issue.type} />
            <PriorityBadge priority={issue.priority} />
            <StoryPointBadge value={issue.estimateStoryPoints} />
          </StyledInformationContainer>
        </StyledTile>
      )}
    </Draggable>
  );
};
