import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IFleets } from 'app/shared/model/fleets.model';
import { getEntities as getFleets } from 'app/entities/fleets/fleets.reducer';
import { getEntity, updateEntity, createEntity, reset } from './cars.reducer';
import { ICars } from 'app/shared/model/cars.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const CarsUpdate = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const fleets = useAppSelector(state => state.fleets.entities);
  const carsEntity = useAppSelector(state => state.cars.entity);
  const loading = useAppSelector(state => state.cars.loading);
  const updating = useAppSelector(state => state.cars.updating);
  const updateSuccess = useAppSelector(state => state.cars.updateSuccess);

  const handleClose = () => {
    props.history.push('/cars');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(props.match.params.id));
    }

    dispatch(getFleets({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    values.createdAt = convertDateTimeToServer(values.createdAt);
    values.updatedAt = convertDateTimeToServer(values.updatedAt);

    const entity = {
      ...carsEntity,
      ...values,
      fleetId: fleets.find(it => it.id.toString() === values.fleetIdId.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          createdAt: displayDefaultDateTime(),
          updatedAt: displayDefaultDateTime(),
        }
      : {
          ...carsEntity,
          createdAt: convertDateTimeFromServer(carsEntity.createdAt),
          updatedAt: convertDateTimeFromServer(carsEntity.updatedAt),
          fleetIdId: carsEntity?.fleetId?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="goDriveGatewayApp.cars.home.createOrEditLabel" data-cy="CarsCreateUpdateHeading">
            <Translate contentKey="goDriveGatewayApp.cars.home.createOrEditLabel">Create or edit a Cars</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="cars-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField label={translate('goDriveGatewayApp.cars.name')} id="cars-name" name="name" data-cy="name" type="text" />
              <ValidatedField
                label={translate('goDriveGatewayApp.cars.description')}
                id="cars-description"
                name="description"
                data-cy="description"
                type="text"
              />
              <ValidatedField
                label={translate('goDriveGatewayApp.cars.carPlate')}
                id="cars-carPlate"
                name="carPlate"
                data-cy="carPlate"
                type="text"
              />
              <ValidatedField label={translate('goDriveGatewayApp.cars.vin')} id="cars-vin" name="vin" data-cy="vin" type="text" />
              <ValidatedField
                label={translate('goDriveGatewayApp.cars.createdAt')}
                id="cars-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('goDriveGatewayApp.cars.updatedAt')}
                id="cars-updatedAt"
                name="updatedAt"
                data-cy="updatedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                id="cars-fleetId"
                name="fleetIdId"
                data-cy="fleetId"
                label={translate('goDriveGatewayApp.cars.fleetId')}
                type="select"
              >
                <option value="" key="0" />
                {fleets
                  ? fleets.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/cars" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default CarsUpdate;
