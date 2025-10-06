import { Component } from '@angular/core';
import { FormArray, FormBuilder, FormControl, FormGroup, ReactiveFormsModule, ValidatorFn, Validators } from '@angular/forms';
import { FieldService } from '../shared/services/field.service';
import { UrlService } from '../shared/services/url.service';
import { ApiService } from '../shared/services/api.service';
import { MethodTypeDto, UrlDto } from '../shared/models/url.model';
import { FieldDto } from '../shared/models/field.model';
import { FieldType, mapFieldTypeToLabel } from '../shared/constants/field-type.constant';
import { booleanValidator } from '../shared/validators/boolean.validator';
import { datetimeValidator } from '../shared/validators/datetime.validator';
import { decimalValidator, integerValidator } from '../shared/validators/number.validator';
import { mapMethodTypeToLabel, MethodType } from '../shared/constants/method-type.constant';
import { debounceTime } from 'rxjs';
import { HttpResponse } from '@angular/common/http';
import { JsonPipe } from '@angular/common';
import { toolErrorLabels } from '../shared/translations/tool.translation';
import { AuthService } from '../shared/services/auth.service';
import { Router } from '@angular/router';
import { notAuthUrlValidator } from '../shared/validators/not-auth-url.validator';

@Component({
  selector: 'app-tool',
  templateUrl: './tool.component.html',
  imports: [ReactiveFormsModule, JsonPipe]
})
export class ToolComponent {
  constructor(private fieldService: FieldService, private urlService: UrlService,
    private apiService: ApiService, private fb: FormBuilder, private authService: AuthService,
    private router: Router) {
  }

  fieldsForm!: FormGroup;
  urlForm!: FormGroup;
  searchControl = new FormControl('');

  allPossibleUrls: UrlDto[] = [];
  filteredPossibleUrls: UrlDto[] = [];

  methods: MethodTypeDto[] = [];

  url!: UrlDto;
  fields: FieldDto[] = [];

  responseStatus!: number;
  response: any;
  showResponse = false;

  urlError = false;
  urlErrorMessage = '';

  notIntegerErrorMessage = toolErrorLabels['field-not-integer'];
  notDecimalErrorMessage = toolErrorLabels['field-not-decimal'];
  notDatetimeErrorMessage = toolErrorLabels['field-not-datetime'];
  notBooleanErrorMessage = toolErrorLabels['field-not-boolean'];
  requiredErrorMessage = toolErrorLabels['field-required'];
  authUrlErrorMessage = toolErrorLabels['auth-url'];

  urlRequiredErrorMessage = toolErrorLabels['url-required'];
  methodRequiredErrorMessage = toolErrorLabels['method-required'];

  ngOnInit() {
    this.urlForm = this.fb.group({
      url: ['', [Validators.required, notAuthUrlValidator()]],
      method: ['', Validators.required]
    });

    this.urlForm.get('url')?.valueChanges
      .pipe(debounceTime(500))
      .subscribe((value: string | null) => {
        if (value !== null && value.length >= 6) {
          this.getUrlDtoByUrl();
        }
      })

    this.urlForm.get('method')?.valueChanges
      .pipe(debounceTime(500))
      .subscribe((value: string | null) => {
        if (this.urlForm.value.url !== null && this.urlForm.value.url.length >= 6) {
          this.getUrlDtoByUrl();
        }
      })

    this.searchControl.valueChanges
      .pipe(debounceTime(500))
      .subscribe((value: string | null) => {
        if (value !== null)
          this.filteredPossibleUrls = this.filterAllPossibleUrls(value);
      });

    this.urlService.getAll().subscribe({
      next: (urlDtos: UrlDto[]) => {
        this.allPossibleUrls = urlDtos;
        this.filteredPossibleUrls = this.allPossibleUrls;
      }
    });

    this.urlService.getMethods().subscribe({
      next: (methodTypeDto: MethodTypeDto[]) => {
        this.methods = methodTypeDto;
      }
    })
  }

  get fieldsFormControls(): FormArray {
    return this.fieldsForm.get('fields') as FormArray;
  }

  get fieldsIndices(): number[] {
    return Array(this.fieldsFormControls.length).fill(0).map((_, i) => i);
  }

  getUrlDtoByUrl() {
    if (this.urlForm.invalid) {
      this.urlError = false;
      this.validateForm(this.urlForm);
      return;
    }

    this.urlService.getByUrl(this.urlForm.value.url, this.urlForm.value.method).subscribe({
      next: (urlDto: UrlDto[]) => {
        this.url = urlDto[0];
        this.urlError = false;

        this.getFieldsForUrl();
      },
      error: (error: any) => {
        if (error.error.status === 404) {
          this.urlError = true;
          this.urlErrorMessage = error.error.message;
          this.fields = [];
          this.showResponse = false;
        }
      }
    })
  }

  getFieldsForUrl() {
    this.fieldService.getByUrlId(this.url.id).subscribe({
      next: (fieldDtos: FieldDto[]) => {
        this.fields = fieldDtos;

        this.fieldsForm = this.fb.group({
          fields: this.fb.array([])
        });

        for (let fieldDto of fieldDtos) {
          this.fieldsFormControls.push(new FormControl('', this.getValidatorsForField(fieldDto)));
        }
      }
    })
  }

  sendRequest() {
    this.validateForm(this.urlForm);

    if (this.urlForm.invalid) {
      return;
    }

    if (this.fields.length > 0) {
      this.validateForm(this.fieldsForm);

      if (this.fieldsForm.invalid) {
        return;
      }
    }

    let queryOrBody = {};
    for (let i = 0; i < this.fields.length; i++) {
      Object.assign(queryOrBody, { [this.fields[i].name]: this.fieldsFormControls.at(i).value })
    }

    this.showResponse = true;
    this.apiService.sendRequest(this.urlForm.value.url, this.urlForm.value.method, queryOrBody).subscribe({
      next: (resp: HttpResponse<any>) => {
        this.responseStatus = resp.status;
        if (resp.body)
          this.response = resp.body;
      },
      error: (err) => {
        this.responseStatus = err.status;
        if (err.error)
          this.response = err.error;
      }
    })
  }

  logout() {
    this.authService.logout();
    this.router.navigateByUrl("/apitool/auth/signin");
  }

  getValidatorsForField(fieldDto: FieldDto): ValidatorFn[] {
    let validators: ValidatorFn[] = [];
    if (fieldDto.required)
      validators.push(Validators.required)

    switch (fieldDto.type) {
      case FieldType.BOOLEAN:
        validators.push(booleanValidator());
        break;
      case FieldType.DATETIME:
        validators.push(datetimeValidator());
        break;
      case FieldType.DECIMAL:
        validators.push(decimalValidator());
        break;
      case FieldType.ENUM:
      case FieldType.INTEGER:
        validators.push(integerValidator());
        break;
    }
    return validators;
  }

  validateForm(form: FormGroup) {
    if (form.invalid) {
      form.markAllAsTouched();
      return;
    }
  }

  getMethodTypeLabel(methodType: MethodType) {
    return mapMethodTypeToLabel(methodType);
  }

  filterAllPossibleUrls(searchValue: string) {
    return this.allPossibleUrls.filter(url =>
      url.description.toLowerCase().includes(searchValue.toLowerCase())
      || url.url.toLowerCase().includes(searchValue.toLowerCase()));
  }

  isDatetime(field: FieldDto) {
    return field.type === FieldType.DATETIME;
  }

  mapFieldTypeToLabel(fieldType: FieldType) {
    return mapFieldTypeToLabel(fieldType);
  }
}
