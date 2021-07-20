import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Cars from './cars';
import CarsDetail from './cars-detail';
import CarsUpdate from './cars-update';
import CarsDeleteDialog from './cars-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={CarsUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={CarsUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={CarsDetail} />
      <ErrorBoundaryRoute path={match.url} component={Cars} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={CarsDeleteDialog} />
  </>
);

export default Routes;
