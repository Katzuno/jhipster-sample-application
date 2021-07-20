import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './codes.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const CodesDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const codesEntity = useAppSelector(state => state.codes.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="codesDetailsHeading">
          <Translate contentKey="goDriveGatewayApp.codes.detail.title">Codes</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{codesEntity.id}</dd>
          <dt>
            <span id="mode">
              <Translate contentKey="goDriveGatewayApp.codes.mode">Mode</Translate>
            </span>
          </dt>
          <dd>{codesEntity.mode}</dd>
          <dt>
            <span id="segment">
              <Translate contentKey="goDriveGatewayApp.codes.segment">Segment</Translate>
            </span>
          </dt>
          <dd>{codesEntity.segment}</dd>
          <dt>
            <span id="code">
              <Translate contentKey="goDriveGatewayApp.codes.code">Code</Translate>
            </span>
          </dt>
          <dd>{codesEntity.code}</dd>
          <dt>
            <span id="dimension">
              <Translate contentKey="goDriveGatewayApp.codes.dimension">Dimension</Translate>
            </span>
          </dt>
          <dd>{codesEntity.dimension}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="goDriveGatewayApp.codes.description">Description</Translate>
            </span>
          </dt>
          <dd>{codesEntity.description}</dd>
          <dt>
            <span id="minVal">
              <Translate contentKey="goDriveGatewayApp.codes.minVal">Min Val</Translate>
            </span>
          </dt>
          <dd>{codesEntity.minVal}</dd>
          <dt>
            <span id="maxVal">
              <Translate contentKey="goDriveGatewayApp.codes.maxVal">Max Val</Translate>
            </span>
          </dt>
          <dd>{codesEntity.maxVal}</dd>
          <dt>
            <span id="units">
              <Translate contentKey="goDriveGatewayApp.codes.units">Units</Translate>
            </span>
          </dt>
          <dd>{codesEntity.units}</dd>
          <dt>
            <span id="enabled">
              <Translate contentKey="goDriveGatewayApp.codes.enabled">Enabled</Translate>
            </span>
          </dt>
          <dd>{codesEntity.enabled ? 'true' : 'false'}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="goDriveGatewayApp.codes.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>{codesEntity.createdAt ? <TextFormat value={codesEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="updatedAt">
              <Translate contentKey="goDriveGatewayApp.codes.updatedAt">Updated At</Translate>
            </span>
          </dt>
          <dd>{codesEntity.updatedAt ? <TextFormat value={codesEntity.updatedAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
        </dl>
        <Button tag={Link} to="/codes" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/codes/${codesEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CodesDetail;
