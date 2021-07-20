import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity, updateEntity, createEntity, reset } from './codes.reducer';
import { ICodes } from 'app/shared/model/codes.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const CodesUpdate = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const codesEntity = useAppSelector(state => state.codes.entity);
  const loading = useAppSelector(state => state.codes.loading);
  const updating = useAppSelector(state => state.codes.updating);
  const updateSuccess = useAppSelector(state => state.codes.updateSuccess);

  const handleClose = () => {
    props.history.push('/codes');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(props.match.params.id));
    }
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
      ...codesEntity,
      ...values,
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
          ...codesEntity,
          createdAt: convertDateTimeFromServer(codesEntity.createdAt),
          updatedAt: convertDateTimeFromServer(codesEntity.updatedAt),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="goDriveGatewayApp.codes.home.createOrEditLabel" data-cy="CodesCreateUpdateHeading">
            <Translate contentKey="goDriveGatewayApp.codes.home.createOrEditLabel">Create or edit a Codes</Translate>
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
                  id="codes-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField label={translate('goDriveGatewayApp.codes.mode')} id="codes-mode" name="mode" data-cy="mode" type="text" />
              <ValidatedField
                label={translate('goDriveGatewayApp.codes.segment')}
                id="codes-segment"
                name="segment"
                data-cy="segment"
                type="text"
              />
              <ValidatedField label={translate('goDriveGatewayApp.codes.code')} id="codes-code" name="code" data-cy="code" type="text" />
              <ValidatedField
                label={translate('goDriveGatewayApp.codes.dimension')}
                id="codes-dimension"
                name="dimension"
                data-cy="dimension"
                type="text"
              />
              <ValidatedField
                label={translate('goDriveGatewayApp.codes.description')}
                id="codes-description"
                name="description"
                data-cy="description"
                type="text"
              />
              <ValidatedField
                label={translate('goDriveGatewayApp.codes.minVal')}
                id="codes-minVal"
                name="minVal"
                data-cy="minVal"
                type="text"
              />
              <ValidatedField
                label={translate('goDriveGatewayApp.codes.maxVal')}
                id="codes-maxVal"
                name="maxVal"
                data-cy="maxVal"
                type="text"
              />
              <ValidatedField
                label={translate('goDriveGatewayApp.codes.units')}
                id="codes-units"
                name="units"
                data-cy="units"
                type="text"
              />
              <ValidatedField
                label={translate('goDriveGatewayApp.codes.enabled')}
                id="codes-enabled"
                name="enabled"
                data-cy="enabled"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('goDriveGatewayApp.codes.createdAt')}
                id="codes-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('goDriveGatewayApp.codes.updatedAt')}
                id="codes-updatedAt"
                name="updatedAt"
                data-cy="updatedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/codes" replace color="info">
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

export default CodesUpdate;
