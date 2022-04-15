import React from 'react';
import { Route, Routes as ReactRoutes } from 'react-router-dom';
import { useDispatch } from 'react-redux';

import { CustomRouter, history } from 'Routes';
import { GlobalErrorBoundary } from 'Modules/GlobalErrorBoundary';
import { LandingPage } from 'Modules/LandingPage';
import { Login, Signup, SignupConfirmation, SuccessNotification } from 'Modules/Auth';
import { Dashboard } from 'Modules/Dashboard';
import { ProjectPage } from 'Modules/ProjectPage';
import { UnassignedUserPage } from 'Modules/UnassignedUserPage';
import { User, UserModel } from 'Models/UserModel';
import { actionCreators } from 'Stores/User';
import { NOTIFICATION_TYPES } from 'Shared/constants';
import { AppHeader } from './components/AppHeader/AppHeader';
import { AppSidebar } from './components/AppSidebar/AppSidebar';
import { AppMainWindow } from './components/AppMainWindow/AppMainWindow';
import { HorizontalWrapper, LayoutWrapper } from './MainLayout.styled';

export const MainLayout = (): JSX.Element => {
  const [currentUser, setCurrentUser] = React.useState<User>({
    email: '',
    firstName: '',
    lastName: '',
    username: '',
    accessToken: '',
    authorities: []
  });

  const dispatch = useDispatch();

  React.useEffect(() => {
    UserModel.currentUser.subscribe((user: User) => {
      setCurrentUser(user);
    });
  }, [UserModel.currentUser]);

  React.useEffect(() => {
    if (!currentUser.accessToken) {
      dispatch(actionCreators.refresh());
    }
  }, []);

  const renderForUserWithRole = (): JSX.Element => (
    <React.Fragment>
      <AppSidebar />
      <AppMainWindow>
        <GlobalErrorBoundary>
          <ReactRoutes>
            <Route path={'/'} element={<Dashboard />} />
            <Route path={'/project/:id'} element={<ProjectPage />} />
          </ReactRoutes>
        </GlobalErrorBoundary>
      </AppMainWindow>
    </React.Fragment>
  );

  const renderForUserWithNoRole = (): JSX.Element => (
    <AppMainWindow>
      <GlobalErrorBoundary>
        <ReactRoutes>
          <Route path={'/'} element={<UnassignedUserPage />} />
        </ReactRoutes>
      </GlobalErrorBoundary>
    </AppMainWindow>
  );

  const renderLoggedInView = (): JSX.Element => (
    <HorizontalWrapper>
      {currentUser.authorities.length ? renderForUserWithRole() : renderForUserWithNoRole()}
    </HorizontalWrapper>
  );

  const renderLoggedOutView = (): JSX.Element => (
    <GlobalErrorBoundary>
      <ReactRoutes>
        <Route path={'/'} element={<LandingPage />} />
        <Route path={'/login'} element={<Login />} />
        <Route path={'/signup'} element={<Signup />} />
        <Route path={'/signup_confirmation'} element={<SignupConfirmation />} />
        <Route
          path={'/signup_success'}
          element={<SuccessNotification type={NOTIFICATION_TYPES.SIGNUP} />}
        />
      </ReactRoutes>
    </GlobalErrorBoundary>
  );

  return (
    <CustomRouter basename={'/'} history={history}>
      <LayoutWrapper>
        <AppHeader isLoggedIn={Boolean(currentUser.accessToken)} />
        {currentUser.accessToken ? renderLoggedInView() : renderLoggedOutView()}
      </LayoutWrapper>
    </CustomRouter>
  );
};
