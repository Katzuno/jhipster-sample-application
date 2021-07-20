import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './cars.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const CarsDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const carsEntity = useAppSelector(state => state.cars.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="carsDetailsHeading">
          <Translate contentKey="goDriveGatewayApp.cars.detail.title">Cars</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{carsEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="goDriveGatewayApp.cars.name">Name</Translate>
            </span>
          </dt>
          <dd>{carsEntity.name}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="goDriveGatewayApp.cars.description">Description</Translate>
            </span>
          </dt>
          <dd>{carsEntity.description}</dd>
          <dt>
            <span id="carPlate">
              <Translate contentKey="goDriveGatewayApp.cars.carPlate">Car Plate</Translate>
            </span>
          </dt>
          <dd>{carsEntity.carPlate}</dd>
          <dt>
            <span id="vin">
              <Translate contentKey="goDriveGatewayApp.cars.vin">Vin</Translate>
            </span>
          </dt>
          <dd>{carsEntity.vin}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="goDriveGatewayApp.cars.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>{carsEntity.createdAt ? <TextFormat value={carsEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="updatedAt">
              <Translate contentKey="goDriveGatewayApp.cars.updatedAt">Updated At</Translate>
            </span>
          </dt>
          <dd>{carsEntity.updatedAt ? <TextFormat value={carsEntity.updatedAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <Translate contentKey="goDriveGatewayApp.cars.fleetId">Fleet Id</Translate>
          </dt>
          <dd>{carsEntity.fleetId ? carsEntity.fleetId.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/cars" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/cars/${carsEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CarsDetail;
