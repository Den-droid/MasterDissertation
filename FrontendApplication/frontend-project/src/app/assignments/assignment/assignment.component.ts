import { CommonModule, Location } from '@angular/common';
import { Component } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AssignmentService } from '../../shared/services/assignment.service';
import { JWTTokenService } from '../../shared/services/jwt-token.service';
import { AssignmentAnswerDto, AssignmentDto, AssignmentResponseDto } from '../../shared/models/assignment.model';
import { assignmentLabels } from '../../shared/translations/assignment.translation';
import { joinVariables } from '../../shared/helpers/variables-join.helper';
import { AnswerDto } from '../../shared/models/answer.model';
import { AssignmentStatus } from '../../shared/constants/assignment-status.constant';

@Component({
  selector: 'app-assignment',
  templateUrl: './assignment.component.html',
  styleUrl: './assignment.component.css',
  imports: [CommonModule, FormsModule, ReactiveFormsModule]
})
export class AssignmentComponent {
  constructor(public assignmentService: AssignmentService, public jwtService: JWTTokenService,
    public router: Router, private route: ActivatedRoute, private fb: FormBuilder, private location: Location) {
  }

  form!: FormGroup;

  assignmentId!: number;
  assignmentDto!: AssignmentDto;
  assignmentResponseDto!: AssignmentResponseDto;
  previousAnswersDto: AnswerDto[] = [];
  variablesErrorRequired = assignmentLabels['variable-empty'];
  variablesErrorNotNumber = assignmentLabels['variable-not-number'];
  isAnswersMode = false;

  ngOnInit() {
    this.form = this.fb.group({
      variables: this.fb.array([])
    });

    this.route.queryParamMap.subscribe(params => {
      this.isAnswersMode = params.get('answerMode')?.toLowerCase() === "true";
    });

    this.route.paramMap.subscribe(params => {
      this.assignmentId = Number(params.get('id'));

      this.getById();
      this.getPreviousAnswers();
    });
  }

  get variables(): FormArray {
    return this.form.get('variables') as FormArray;
  }

  get variablesIndices(): number[] {
    return Array(this.variables.length).fill(0).map((_, i) => i);
  }

  getById() {
    this.assignmentService.getById(this.assignmentId).subscribe({
      next: (assignmentDto: AssignmentDto) => {
        this.assignmentDto = assignmentDto;
        for (let i = 0; i < this.assignmentDto.variablesCount; i++) {
          this.variables.push(this.fb.control('', [Validators.required, Validators.pattern(/^\d+(\.\d+)?$/)]));
        }

        if (this.assignmentDto.statusId === AssignmentStatus.FINISHED) {
          this.isAnswersMode = true;
        }
      },
      error: (error: any) => {
        this.router.navigate([`/error/404`]);
      }
    })
  }

  getPreviousAnswers() {
    this.assignmentService.getAnswers(this.assignmentId).subscribe({
      next: (answerDtos: AnswerDto[]) => {
        this.previousAnswersDto = answerDtos;
        if (this.previousAnswersDto.length > 0) {
          this.assignmentResponseDto = new AssignmentResponseDto(this.previousAnswersDto[0].result,
            this.assignmentDto.attemptRemaining, this.previousAnswersDto[0].isCorrect)
        }
      }
    })
  }

  answer() {
    this.onValidate();

    if (this.form.invalid) {
      return;
    }

    let variableNames = [], variableValues = [];
    for (let i = 0; i < this.variables.length; i++) {
      variableNames.push(`x${i + 1}`);
      variableValues.push(this.variables.at(i).value);
    }
    let answer = joinVariables(variableNames, variableValues);

    this.assignmentService.answer(this.assignmentId, new AssignmentAnswerDto(answer))
      .subscribe({
        next: (assignmentResponseDto: AssignmentResponseDto) => {
          this.assignmentResponseDto = assignmentResponseDto;
          this.assignmentDto.attemptRemaining = this.assignmentResponseDto.attemptsRemaining;

          this.getPreviousAnswers();
        }
      })

  }

  finish() {
    this.assignmentService.finish(this.assignmentId).subscribe({
      next: () => {
        this.router.navigate([`/student/assignments`]);
      }
    })
  }

  onValidate() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
    }
  }

  disableInputsAndButtons() {
    return (this.assignmentResponseDto && this.assignmentResponseDto.hasCorrectAnswer)
      || (this.assignmentDto && this.assignmentDto.attemptRemaining === 0);
  }

  goBack() {
    this.location.back();
  }
}
