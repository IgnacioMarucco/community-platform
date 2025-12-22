import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';
import {
  PASSWORD_MIN_LENGTH,
  PASSWORD_SPECIAL_CHARS
} from './validation.constants';

const UPPERCASE_REGEX = /[A-Z]/;
const LOWERCASE_REGEX = /[a-z]/;
const NUMBER_REGEX = /\d/;
const SPECIAL_REGEX = /[@$!%*?&#]/;

export type PasswordRuleKey = 'minLength' | 'uppercase' | 'lowercase' | 'number' | 'special';

export const PASSWORD_RULES: ReadonlyArray<{ key: PasswordRuleKey; label: string }> = [
  { key: 'minLength', label: `At least ${PASSWORD_MIN_LENGTH} characters` },
  { key: 'uppercase', label: 'One uppercase letter' },
  { key: 'lowercase', label: 'One lowercase letter' },
  { key: 'number', label: 'One number' },
  { key: 'special', label: `One special character (${PASSWORD_SPECIAL_CHARS})` }
];

export function passwordValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const value = (control.value ?? '') as string;

    if (!value) {
      return null;
    }

    const errors: ValidationErrors = {};

    if (value.length < PASSWORD_MIN_LENGTH) {
      errors['minLength'] = {
        requiredLength: PASSWORD_MIN_LENGTH,
        actualLength: value.length
      };
    }

    if (!UPPERCASE_REGEX.test(value)) {
      errors['uppercase'] = true;
    }

    if (!LOWERCASE_REGEX.test(value)) {
      errors['lowercase'] = true;
    }

    if (!NUMBER_REGEX.test(value)) {
      errors['number'] = true;
    }

    if (!SPECIAL_REGEX.test(value)) {
      errors['special'] = true;
    }

    return Object.keys(errors).length ? errors : null;
  };
}
