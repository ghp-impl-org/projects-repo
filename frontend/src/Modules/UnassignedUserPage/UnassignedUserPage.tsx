import React from 'react';
import { shallowEqual, useDispatch, useSelector } from 'react-redux';
import { useTranslation } from 'react-i18next';
import { Heading5, Heading6 } from 'Shared/Typography';
import { OrganisationForm } from 'Shared/Forms/OrganisationForm';
import { Container, useLoading } from 'Hooks/useLoading';
import { OrganisationPayload } from 'Types/Organisation';
import { Api } from 'Utils/Api';
import { actionCreators as userActionCreators } from 'Stores/User';
import { actionCreators as invitationsActionCreators } from 'Stores/OrganisationInvitation';

import {
  StyledFormContainer,
  StyledHorizontalContainer,
  StyledInvitationsContainer,
  StyledPageWrapper
} from './UnassignedUserPage.styled';
import { ApplicationState } from '../../Stores/store';
import { InvitationPanel } from './components';

export const UnassignedUserPage = (): JSX.Element => {
  const { t } = useTranslation();
  const dispatch = useDispatch();
  const { isLoading, startLoading, stopLoading } = useLoading();
  const userInvitations = useSelector(
    (state: ApplicationState) =>
      state.organisationInvitation ? state.organisationInvitation.userInvitations : [],
    shallowEqual
  );

  React.useEffect(() => {
    dispatch(invitationsActionCreators.getUserInvitations());
  }, []);

  const handleCreatingOrganisation = async (data: OrganisationPayload) => {
    startLoading();

    const result = await Api.post('organisations', data);

    stopLoading();
    if (result.status === 201) {
      dispatch(userActionCreators.refresh());
    }
  };

  const handleSendingInvitationResponseFactory =
    (organisationId: Id, shouldAccept: boolean) => async () => {
      startLoading();
      const result = await Api.post(
        `organisations/${organisationId}/${shouldAccept ? 'accept' : 'decline'}_invitation`
      );

      if (result.status === 200) {
        if (shouldAccept) {
          dispatch(userActionCreators.refresh());
        } else {
          dispatch(invitationsActionCreators.getUserInvitations());
        }
      }

      stopLoading();
    };

  const renderInvitations = (): JSX.Element[] | JSX.Element => {
    if (userInvitations.length) {
      return userInvitations.map((invitation) => (
        <InvitationPanel
          handleSendingInvitationResponse={handleSendingInvitationResponseFactory}
          key={invitation.id}
          invitation={invitation}
        />
      ));
    }

    return <p>{t('unassignedUserPage.noInvitations')}</p>;
  };

  return (
    <StyledPageWrapper>
      <Container isLoading={isLoading} />
      <Heading5>{t('unassignedUserPage.title')}</Heading5>
      <p>{t('unassignedUserPage.content')}</p>
      <StyledHorizontalContainer>
        <StyledFormContainer>
          <Heading6>{t('unassignedUserPage.createNewOrganisation')}</Heading6>
          <OrganisationForm handleSubmit={handleCreatingOrganisation} />
        </StyledFormContainer>
        <StyledInvitationsContainer>
          <Heading6>{t('unassignedUserPage.invitationsTitle')}</Heading6>
          {renderInvitations()}
        </StyledInvitationsContainer>
      </StyledHorizontalContainer>
    </StyledPageWrapper>
  );
};
