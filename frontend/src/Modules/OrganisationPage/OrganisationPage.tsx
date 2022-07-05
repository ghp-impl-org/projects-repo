import React from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useTranslation } from 'react-i18next';
import { DialogComponent, useDialog } from 'Hooks/useDialog';
import {
  VerticalPageWrapper,
  StyledHorizontalContainer,
  StyledSectionContainer
} from 'Shared/PageWrappers';
import { Heading5 } from 'Shared/Typography';
import { USER_ROLES } from 'Shared/constants';
import { UserModel } from 'Models/UserModel';
import { ApplicationState } from 'Stores/store';
import { actionCreators as userActionCreators } from 'Stores/User';
import { actionCreators as invitationActionCreators } from 'Stores/OrganisationInvitation';
import { Api } from 'Utils/Api';
import { ProjectDialog } from '../ProjectDialog';
import { PROJECT_DIALOG_MODES } from '../ProjectDialog/fixtures';
import {
  OrganisationInfoSection,
  OrganisationInvitationsSection,
  InviteMemberDialog,
  OrganisationMembersSection,
  ProjectsSection
} from './components';
import { INVITE_MEMBER_DIALOG_MODES } from './components/InviteMemberDialog/fixtures';

export const OrganisationPage = (): JSX.Element => {
  const { t } = useTranslation();
  const dispatch = useDispatch();

  const inviteDialogConfig = useDialog<INVITE_MEMBER_DIALOG_MODES>(INVITE_MEMBER_DIALOG_MODES.ADD);
  const projectDialogConfig = useDialog<PROJECT_DIALOG_MODES>(PROJECT_DIALOG_MODES.ADD);

  const isUserOrganisationAdmin = [USER_ROLES.ROLE_ORGANISATION_ADMIN, USER_ROLES.ROLE_ADMIN].some(
    (role: USER_ROLES) => UserModel.currentUserValue.authorities.includes(role)
  );

  React.useEffect(() => {
    dispatch(userActionCreators.getOrganisation());
  }, []);

  const organisationData: Nullable<Organisation.OrganisationEntity> = useSelector(
    (state: ApplicationState) => (state.user ? state.user.organisation : null)
  );

  const invitationsData: Nullable<Organisation.OrganisationInvitation[]> = useSelector(
    (state: ApplicationState) =>
      state.organisationInvitation ? state.organisationInvitation.allInvitations : null
  );

  React.useEffect(() => {
    if (organisationData && organisationData.id && isUserOrganisationAdmin) {
      dispatch(invitationActionCreators.getAllInvitationsForOrganisation(organisationData.id));
    }
  }, [organisationData?.id]);

  const handleOpeningInviteDialog = (): void => {
    inviteDialogConfig.handleOpen(INVITE_MEMBER_DIALOG_MODES.ADD);
  };

  const handleOpeningAddProject = () => {
    projectDialogConfig.handleOpen(PROJECT_DIALOG_MODES.ADD);
  };

  const handleRevokingInvitation = (id: Id) => async () => {
    if (organisationData) {
      const result = await Api.post(`organisations/${organisationData.id}/revoke_invitation`, {
        userId: id
      });

      if (result.status === 200) {
        dispatch(invitationActionCreators.getAllInvitationsForOrganisation(organisationData.id));
      }
    }
  };

  const renderOrganisationInfo = (): Nullable<JSX.Element> => {
    if (organisationData) {
      return <OrganisationInfoSection organisationData={organisationData} />;
    }

    return null;
  };

  const renderProjects = (): Nullable<JSX.Element> => {
    if (organisationData) {
      return (
        <ProjectsSection
          projectsData={organisationData.projects}
          handleOpeningAddProject={handleOpeningAddProject}
        />
      );
    }

    return null;
  };

  const renderOrganisationMembers = (): Nullable<JSX.Element> => {
    if (organisationData) {
      return <OrganisationMembersSection membersData={organisationData.members} />;
    }

    return null;
  };

  const renderInvitations = (): Nullable<JSX.Element> => {
    if (invitationsData && isUserOrganisationAdmin) {
      return (
        <OrganisationInvitationsSection
          invitationsData={invitationsData}
          handleOpeningInviteDialog={handleOpeningInviteDialog}
          handleRevokingInvitation={handleRevokingInvitation}
        />
      );
    }

    return null;
  };

  const renderDialogs = (): JSX.Element => (
    <React.Fragment>
      <DialogComponent
        isOpen={inviteDialogConfig.isOpen}
        handleClose={inviteDialogConfig.handleClose}
      >
        <InviteMemberDialog
          mode={inviteDialogConfig.dialogMode}
          handleClose={inviteDialogConfig.handleClose}
          organisationId={organisationData?.id}
        />
      </DialogComponent>
      <DialogComponent
        isOpen={projectDialogConfig.isOpen}
        handleClose={projectDialogConfig.handleClose}
      >
        <ProjectDialog
          mode={projectDialogConfig.dialogMode}
          handleClose={projectDialogConfig.handleClose}
        />
      </DialogComponent>
    </React.Fragment>
  );

  return (
    <VerticalPageWrapper alignItems="unset">
      <Heading5>{t('organisationPage.title')}</Heading5>
      <StyledHorizontalContainer>
        <StyledSectionContainer>
          {renderOrganisationInfo()}
          {renderProjects()}
        </StyledSectionContainer>
        <StyledSectionContainer>
          {renderOrganisationMembers()}
          {renderInvitations()}
        </StyledSectionContainer>
      </StyledHorizontalContainer>
      {renderDialogs()}
    </VerticalPageWrapper>
  );
};