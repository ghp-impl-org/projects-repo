import React from 'react';
import { useTranslation } from 'react-i18next';
import { IconButton } from 'Shared/Elements/Buttons';
import { StyledFlexContainer } from 'Shared/SharedStyles.styled';
import { Organisation } from 'Types/Organisation';
import { StyledDate, StyledInfoContainer, StyledPanelContainer } from './InvitationPanel.styled';

interface Props {
  invitation: Organisation.OrganisationInvitation;
  handleSendingInvitationResponse: (
    organisationId: Id,
    shouldAccept: boolean
  ) => VoidFunctionNoArgs;
}

export const InvitationPanel = ({
  invitation,
  handleSendingInvitationResponse
}: Props): JSX.Element => {
  const { t } = useTranslation();
  return (
    <StyledPanelContainer>
      <StyledInfoContainer>
        <p>{invitation.organisation.name}</p>
        <StyledDate>
          {t('general.sentAt')} {new Date(invitation.createdAt).toLocaleString()}
        </StyledDate>
      </StyledInfoContainer>
      <StyledFlexContainer>
        <IconButton
          onClick={handleSendingInvitationResponse(invitation.organisation.id, true)}
          iconName="done"
        >
          {t('general.accept')}
        </IconButton>
        <IconButton
          onClick={handleSendingInvitationResponse(invitation.organisation.id, false)}
          iconName="close"
        >
          {t('general.decline')}
        </IconButton>
      </StyledFlexContainer>
    </StyledPanelContainer>
  );
};
