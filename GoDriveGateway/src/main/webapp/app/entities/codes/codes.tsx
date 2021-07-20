import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Col, Row, Table } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntities } from './codes.reducer';
import { ICodes } from 'app/shared/model/codes.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const Codes = (props: RouteComponentProps<{ url: string }>) => {
  const dispatch = useAppDispatch();

  const codesList = useAppSelector(state => state.codes.entities);
  const loading = useAppSelector(state => state.codes.loading);

  useEffect(() => {
    dispatch(getEntities({}));
  }, []);

  const handleSyncList = () => {
    dispatch(getEntities({}));
  };

  const { match } = props;

  return (
    <div>
      <h2 id="codes-heading" data-cy="CodesHeading">
        <Translate contentKey="goDriveGatewayApp.codes.home.title">Codes</Translate>
        <div className="d-flex justify-content-end">
          <Button className="mr-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="goDriveGatewayApp.codes.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to={`${match.url}/new`} className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="goDriveGatewayApp.codes.home.createLabel">Create new Codes</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {codesList && codesList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>
                  <Translate contentKey="goDriveGatewayApp.codes.id">ID</Translate>
                </th>
                <th>
                  <Translate contentKey="goDriveGatewayApp.codes.mode">Mode</Translate>
                </th>
                <th>
                  <Translate contentKey="goDriveGatewayApp.codes.segment">Segment</Translate>
                </th>
                <th>
                  <Translate contentKey="goDriveGatewayApp.codes.code">Code</Translate>
                </th>
                <th>
                  <Translate contentKey="goDriveGatewayApp.codes.dimension">Dimension</Translate>
                </th>
                <th>
                  <Translate contentKey="goDriveGatewayApp.codes.description">Description</Translate>
                </th>
                <th>
                  <Translate contentKey="goDriveGatewayApp.codes.minVal">Min Val</Translate>
                </th>
                <th>
                  <Translate contentKey="goDriveGatewayApp.codes.maxVal">Max Val</Translate>
                </th>
                <th>
                  <Translate contentKey="goDriveGatewayApp.codes.units">Units</Translate>
                </th>
                <th>
                  <Translate contentKey="goDriveGatewayApp.codes.enabled">Enabled</Translate>
                </th>
                <th>
                  <Translate contentKey="goDriveGatewayApp.codes.createdAt">Created At</Translate>
                </th>
                <th>
                  <Translate contentKey="goDriveGatewayApp.codes.updatedAt">Updated At</Translate>
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {codesList.map((codes, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`${match.url}/${codes.id}`} color="link" size="sm">
                      {codes.id}
                    </Button>
                  </td>
                  <td>{codes.mode}</td>
                  <td>{codes.segment}</td>
                  <td>{codes.code}</td>
                  <td>{codes.dimension}</td>
                  <td>{codes.description}</td>
                  <td>{codes.minVal}</td>
                  <td>{codes.maxVal}</td>
                  <td>{codes.units}</td>
                  <td>{codes.enabled ? 'true' : 'false'}</td>
                  <td>{codes.createdAt ? <TextFormat type="date" value={codes.createdAt} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>{codes.updatedAt ? <TextFormat type="date" value={codes.updatedAt} format={APP_DATE_FORMAT} /> : null}</td>
                  <td className="text-right">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`${match.url}/${codes.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${codes.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${codes.id}/delete`} color="danger" size="sm" data-cy="entityDeleteButton">
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
              <Translate contentKey="goDriveGatewayApp.codes.home.notFound">No Codes found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default Codes;
