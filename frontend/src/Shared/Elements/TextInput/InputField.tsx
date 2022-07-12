import React from 'react';
import { useTranslation } from 'react-i18next';
import { Field, FieldAttributes } from 'formik';
import { StyledReadonlyField } from './TextInput.styled';
import { StyledNoValueMessage } from '../TextArea/TextArea.styled';

interface Props {
  valid?: boolean;
  error?: boolean | '';
  readonly?: boolean;
}

export const InputField = ({
  className,
  valid,
  error,
  readonly,
  ...props
}: FieldAttributes<Props>): JSX.Element => {
  const { t } = useTranslation();
  if (readonly) {
    return (
      <StyledReadonlyField>
        {props.value || <StyledNoValueMessage>{t('general.notSet')}</StyledNoValueMessage>}
      </StyledReadonlyField>
    );
  }

  return <Field className={className} {...props} />;
};
