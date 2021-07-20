import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Col, Row, Table } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntities } from './cars.reducer';
import { ICars } from 'app/shared/model/cars.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const Cars = (props: RouteComponentProps<{ url: string }>) => {
  const dispatch = useAppDispatch();

  const carsList = useAppSelector(state => state.cars.entities);
  const loading = useAppSelector(state => state.cars.loading);

  useEffect(() => {
    dispatch(getEntities({}));
  }, []);

  const handleSyncList = () => {
    dispatch(getEntities({}));
  };

  const { match } = props;

  return (
    <div>
      <h2 id="cars-heading" data-cy="CarsHeading">
        <Translate contentKey="goDriveGatewayApp.cars.home.title">Cars</Translate>
        <div className="d-flex justify-content-end">
          <Button className="mr-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="goDriveGatewayApp.cars.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to={`${match.url}/new`} className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="goDriveGatewayApp.cars.home.createLabel">Create new Cars</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {carsList && carsList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>
                  <Translate contentKey="goDriveGatewayApp.cars.id">ID</Translate>
                </th>
                <th>
                  <Translate contentKey="goDriveGatewayApp.cars.name">Name</Translate>
                </th>
                <th>
                  <Translate contentKey="goDriveGatewayApp.cars.description">Description</Translate>
                </th>
                <th>
                  <Translate contentKey="goDriveGatewayApp.cars.carPlate">Car Plate</Translate>
                </th>
                <th>
                  <Translate contentKey="goDriveGatewayApp.cars.vin">Vin</Translate>
                </th>
                <th>
                  <Translate contentKey="goDriveGatewayApp.cars.createdAt">Created At</Translate>
                </th>
                <th>
                  <Translate contentKey="goDriveGatewayApp.cars.updatedAt">Updated At</Translate>
                </th>
                <th>
                  <Translate contentKey="goDriveGatewayApp.cars.fleetId">Fleet Id</Translate>
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {carsList.map((cars, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`${match.url}/${cars.id}`} color="link" size="sm">
                      {cars.id}
                    </Button>
                  </td>
                  <td>{cars.name}</td>
                  <td>{cars.description}</td>
                  <td>{cars.carPlate}</td>
                  <td>{cars.vin}</td>
                  <td>{cars.createdAt ? <TextFormat type="date" value={cars.createdAt} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>{cars.updatedAt ? <TextFormat type="date" value={cars.updatedAt} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>{cars.fleetId ? <Link to={`fleets/${cars.fleetId.id}`}>{cars.fleetId.id}</Link> : ''}</td>
                  <td className="text-right">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`${match.url}/${cars.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${cars.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${cars.id}/delete`} color="danger" size="sm" data-cy="entityDeleteButton">
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="goDriveGatewayApp.cars.home.notFound">No Cars found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default Cars;
