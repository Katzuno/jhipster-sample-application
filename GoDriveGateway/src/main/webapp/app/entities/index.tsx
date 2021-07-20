import React from 'react';
import { Switch } from 'react-router-dom';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Codes from './codes';
import Cars from './cars';
import Fleets from './fleets';
/* jhipster-needle-add-route-import - JHipster will add routes here */

const Routes = ({ match }) => (
  <div>
    <Switch>
      {/* prettier-ignore */}
      <ErrorBoundaryRoute path={`${match.url}codes`} component={Codes} />
      <ErrorBoundaryRoute path={`${match.url}cars`} component={Cars} />
      <ErrorBoundaryRoute path={`${match.url}fleets`} component={Fleets} />
      {/* jhipster-needle-add-route-path - JHipster will add routes here */}
    </Switch>
  </div>
);

export default Routes;
