import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Codes from './codes';
import CodesDetail from './codes-detail';
import CodesUpdate from './codes-update';
import CodesDeleteDialog from './codes-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={CodesUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={CodesUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={CodesDetail} />
      <ErrorBoundaryRoute path={match.url} component={Codes} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={CodesDeleteDialog} />
  </>
);

export default Routes;
