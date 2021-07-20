import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './fleets.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const FleetsDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const fleetsEntity = useAppSelector(state => state.fleets.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="fleetsDetailsHeading">
          <Translate contentKey="goDriveGatewayApp.fleets.detail.title">Fleets</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{fleetsEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="goDriveGatewayApp.fleets.name">Name</Translate>
            </span>
          </dt>
          <dd>{fleetsEntity.name}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="goDriveGatewayApp.fleets.description">Description</Translate>
            </span>
          </dt>
          <dd>{fleetsEntity.description}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="goDriveGatewayApp.fleets.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>{fleetsEntity.createdAt ? <TextFormat value={fleetsEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="updatedAt">
              <Translate contentKey="goDriveGatewayApp.fleets.updatedAt">Updated At</Translate>
            </span>
          </dt>
          <dd>{fleetsEntity.updatedAt ? <TextFormat value={fleetsEntity.updatedAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
        </dl>
        <Button tag={Link} to="/fleets" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/fleets/${fleetsEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default FleetsDetail;
