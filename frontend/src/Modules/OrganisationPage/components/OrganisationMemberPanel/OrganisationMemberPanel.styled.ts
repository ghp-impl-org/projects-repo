import styled from 'styled-components';
import { StyledCircleContainer } from 'Shared/UserCircle/UserCircle.styled';

export const StyledPanelContainer = styled.div`
  display: flex;
  align-items: center;
  padding: 1rem;

  & ${StyledCircleContainer} {
    margin-right: 1rem;
  }

  &:not(:last-child) {
    margin-bottom: 1rem;
  }
`;

export const StyledNameLabel = styled.div`
  font-size: 1.6rem;
`;
