import React, { HTMLAttributes } from 'react';
import styled, { css } from 'styled-components';
import {
  StyledFlexColumnContainer,
  StyledFlexContainer,
  StyledTextEllipsis
} from 'Shared/SharedStyles.styled';

interface ButtonProps extends HTMLAttributes<HTMLButtonElement> {
  readonly isOpen: boolean;
}

const shouldRenderBorderRadius = (isOpen: boolean) => (isOpen ? '0' : '0.3rem');

const DropdownButtonStyles = css<{ disabled?: boolean } & React.HTMLAttributes<HTMLButtonElement>>`
  width: 100%;
  font-size: 1.6rem;
  height: 3rem;
  background-color: ${(props) => props.theme.surfaceSecondary};
  color: ${(props) => props.theme.primaryText};
  padding: 0.5rem 1rem;
  border: none;
  cursor: pointer;
  transition: all 0.4s ease;
  ${StyledTextEllipsis};

  &:hover {
    background-color: ${(props) => (props.disabled ? props.theme.disabled : props.theme.primary)};
    color: ${(props) => props.theme.textOnPrimary};
  }
`;

export const StyledDropdownContainer = styled(StyledFlexContainer)`
  position: relative;
  width: 20rem;
  margin-right: 1rem;
`;

export const StyledDropdownButton = styled.button<ButtonProps>`
  ${DropdownButtonStyles};
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-radius: 0.3rem 0.3rem ${(props) => shouldRenderBorderRadius(props.isOpen)}
    ${(props) => shouldRenderBorderRadius(props.isOpen)};

  & > span {
    margin-left: auto;
    transition: all 0.4s ease;
    transform: ${(props) => (props.isOpen ? 'rotate(90deg)' : 'none')};
  }
`;

export const StyledDropdownLabelContainer = styled.div`
  ${StyledTextEllipsis};
`;

export const StyledDropdownOptionsList = styled(StyledFlexColumnContainer)`
  width: 100%;
  position: absolute;
  top: 3rem;
  left: 0;
  z-index: 15;
`;

export const StyledDropdownOption = styled.button<ButtonProps>`
  ${DropdownButtonStyles};

  border-top: ${({ isOpen, theme }) => (isOpen ? `1px solid ${theme.primary}` : 'none')};

  &:disabled {
    color: ${(props) => props.theme.disabledText};
  }

  &:last-child {
    border-bottom-left-radius: ${({ isOpen }) => (!isOpen ? '0' : '0.3rem')};
    border-bottom-right-radius: ${({ isOpen }) => (!isOpen ? '0' : '0.3rem')};
  }
`;
