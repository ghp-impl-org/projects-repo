import React from 'react';
import { useTranslation } from 'react-i18next';
import { StyledSectionHeaderRow, StyledSectionWrapper } from 'Shared/PageWrappers';
import { UserPanel } from 'Shared/UserPanel';
import { Heading6 } from 'Shared/Typography';
import { User } from 'Types/User';

interface Props {
  membersData: User.UserBasicEntity[];
}

export const OrganisationMembersSection = ({ membersData }: Props): JSX.Element => {
  const { t } = useTranslation();

  return (
    <StyledSectionWrapper>
      <StyledSectionHeaderRow>
        <Heading6>{t('organisationPage.members')}</Heading6>
      </StyledSectionHeaderRow>
      {membersData.map((user: User.UserBasicEntity) => (
        <UserPanel key={user.id} user={user} />
      ))}
    </StyledSectionWrapper>
  );
};
