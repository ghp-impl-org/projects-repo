import React from 'react';
import { useTranslation } from 'react-i18next';
import { shallowEqual, useSelector } from 'react-redux';
import { SingleSelectDropdown } from 'Shared/Elements/SingleSelectDropdown';
import { CURRENT_PROJECT_KEY } from 'Shared/constants';
import { StyledFlexContainerAlignCenter } from 'Shared/SharedStyles.styled';
import { ApplicationState } from 'Stores/store';
import { Project } from 'Types/Project';
import { CurrentProjectModel } from 'Models/CurrentProjectModel';
import { StyledLabel } from './CurrentProjectField.styled';

export const CurrentProjectField = (): Nullable<JSX.Element> => {
  const [currentProjectValue, setCurrentProjectValue] = React.useState<Id>('');
  const { t } = useTranslation();
  const organisationProjects = useSelector(
    (state: ApplicationState) => (state.project ? state.project.organisationProjects : []),
    shallowEqual
  );

  React.useEffect(() => {
    const subscription = CurrentProjectModel.currentProject.subscribe(
      (project: Project.ProjectEntity) => {
        setCurrentProjectValue(project.id);
      }
    );

    return () => {
      subscription.unsubscribe();
    };
  }, []);

  if (!organisationProjects.length) {
    return null;
  }

  const handleSelectingCurrentProject = (value: Nullable<string>) => {
    const project = organisationProjects.find(
      (project: Project.ProjectEntity) => project.id === value
    );

    if (project) {
      CurrentProjectModel.currentProjectSubject.next(project);
      setCurrentProjectValue(project.id);
      localStorage.setItem(CURRENT_PROJECT_KEY, project.id);
    }
  };

  const mappedProjects = organisationProjects.map((project: Project.ProjectEntity) => ({
    value: project.id,
    label: project.name
  }));

  return (
    <StyledFlexContainerAlignCenter>
      <StyledLabel>{t('header.currentProject')}</StyledLabel>
      <SingleSelectDropdown
        options={mappedProjects}
        value={currentProjectValue}
        onChange={handleSelectingCurrentProject}
      />
    </StyledFlexContainerAlignCenter>
  );
};
