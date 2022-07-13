import styled from 'styled-components';
import React from 'react';

interface ContainerProps extends React.HTMLAttributes<HTMLDivElement> {
  readonly alignItems?: string;
}

export const StyledFieldContainer = styled.div`
  display: flex;
  flex-direction: column;
  margin-bottom: 2rem;

  & input {
    margin-bottom: 0;
  }
`;

export const StyledLabelAndInputContainer = styled.div<ContainerProps>`
  display: flex;
  align-items: ${({ alignItems }) => alignItems ?? 'baseline'};

  & input,
  & textarea {
    width: 100%;
  }
`;

export const StyledChildrenContainer = styled.div`
  width: 100%;
  display: flex;

  & > div {
    &:not(:last-child) {
      margin-right: 1.5rem;
    }
  }
`;

export const StyledLabelContainer = styled.div`
  display: flex;
  justify-content: space-between;
`;

export const StyledFormLabel = styled.p`
  font-size: 1.6rem;
  min-width: 18rem;
`;

export const StyledRequiredMark = styled.span`
  color: ${(props) => props.theme.error};
  font-size: 2rem;
`;
