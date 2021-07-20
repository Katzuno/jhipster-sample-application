import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Fleets from './fleets';
import FleetsDetail from './fleets-detail';
import FleetsUpdate from './fleets-update';
import FleetsDeleteDialog from './fleets-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={FleetsUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={FleetsUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={FleetsDetail} />
      <ErrorBoundaryRoute path={match.url} component={Fleets} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={FleetsDeleteDialog} />
  </>
);

export default Routes;
