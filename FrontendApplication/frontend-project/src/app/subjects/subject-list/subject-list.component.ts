import { Component, OnInit } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { FormsModule } from '@angular/forms';
import { debounceTime, Subject } from 'rxjs';
import { InfoModalComponent } from '../../shared/modals/info/info-modal.component';
import { ConfirmModalComponent } from '../../shared/modals/confirm/confirm-modal.component';
import { modalText } from '../../shared/translations/common.translation';
import { SubjectDto } from '../../shared/models/subject.model';
import { SubjectService } from '../../shared/services/subject.service';
import { SubjectModalComponent } from '../subject-modal/subject-modal.component';
import { subjectModal } from '../../shared/translations/subject.translation';
import { AuthService } from '../../shared/services/auth.service';
import { ActionType } from '../../shared/constants/action-type';
import { UniversityService } from '../../shared/services/university.service';
import { UniversityDto } from '../../shared/models/university.model';
import { AssignmentRestrictionService } from '../../shared/services/assignment-restriction.service';
import { RestrictionModalComponent } from '../../shared/modals/restriction/restriction-modal.component';
import { DefaultRestrictionDto, ModalRestrictionDto, RestrictionDto } from '../../shared/models/restriction.model';
import { restrictionModalHeaders } from '../../shared/translations/restriction.translation';
import { DefaultRestrictionLevel } from '../../shared/constants/default-restriction-level.constant';

@Component({
  selector: 'app-subject-list',
  templateUrl: './subject-list.component.html',
  imports: [FormsModule]
})
export class SubjectListComponent implements OnInit {
  subjects: SubjectDto[] = [];
  filteredSubjects: SubjectDto[] = [];
  editedSubject!: SubjectDto;

  searchQuery: string = '';
  searchSubject: Subject<string> = new Subject<string>();

  constructor(private modalService: NgbModal, private subjectService: SubjectService, private authService: AuthService,
    private universityService: UniversityService, private restrictionService: AssignmentRestrictionService
  ) { }

  ngOnInit(): void {
    this.getSubjects();

    this.searchSubject.pipe(
      debounceTime(500)
    ).subscribe(value => {
      this.filteredSubjects = this.subjects.filter(u =>
        u.name.toLowerCase().includes(value.toLowerCase())
      );
    });
  }

  getSubjectById(id: number) {
    return this.subjectService.getById(id);
  }

  getSubjects() {
    this.subjectService.getAll().subscribe({
      next: (subjectsDto: SubjectDto[]) => {
        this.subjects = subjectsDto;
        this.filteredSubjects = subjectsDto;
      }
    })
  }

  openAddModal() {
    const modalRef = this.modalService.open(SubjectModalComponent, {
      centered: true,
      backdrop: 'static',
      keyboard: false
    });

    const errorSubject = new Subject<string>();

    modalRef.componentInstance.title = subjectModal['add-title'];
    modalRef.componentInstance.errorSubject$ = errorSubject;
    modalRef.componentInstance.actionType = ActionType.CREATE;

    this.universityService.getAll().subscribe({
      next: (universities: UniversityDto[]) => {
        modalRef.componentInstance.universities = universities;
      }
    })

    modalRef.componentInstance.saveAttempt.subscribe(
      (result: any) => {
        this.subjectService.add(result).subscribe({
          error: (error: any) => {
            errorSubject.next(error.error.message);
          },
          complete: () => {
            modalRef.close();
            this.getSubjects();
          }
        })
      }
    );
  }

  openEditModal(id: number) {
    const modalRef = this.modalService.open(SubjectModalComponent, {
      centered: true,
      backdrop: 'static',
      keyboard: false
    });

    this.getSubjectById(id).subscribe({
      next: (subject: SubjectDto) => {
        modalRef.componentInstance.inputValue = subject;
      }
    })

    this.universityService.getAll().subscribe({
      next: (universities: UniversityDto[]) => {
        modalRef.componentInstance.universities = universities;
      }
    })

    const errorSubject = new Subject<string>();

    modalRef.componentInstance.title = subjectModal['edit-title'];
    modalRef.componentInstance.errorSubject$ = errorSubject;
    modalRef.componentInstance.actionType = ActionType.UPDATE;

    modalRef.componentInstance.saveAttempt.subscribe(
      (result: any) => {
        this.subjectService.update(id, result).subscribe({
          error: (error: any) => {
            errorSubject.next(error.error.message);
          },
          complete: () => {
            modalRef.close();
            this.getSubjects();
          }
        })
      }
    );
  }

  deleteSubject(id: number) {
    const modalRef = this.modalService.open(ConfirmModalComponent, {
      centered: true,
      backdrop: 'static',
      keyboard: false
    });

    modalRef.componentInstance.content = modalText['confirm-content']
    modalRef.componentInstance.title = modalText['confirm-title'];

    modalRef.result.then(
      () => {
        this.subjectService.delete(id).subscribe({
          error: (error: any) => {
            const deleteErrorModalRef = this.modalService.open(InfoModalComponent, {
              centered: true,
              backdrop: 'static',
              keyboard: false
            });

            deleteErrorModalRef.componentInstance.content = error.error.message;
            deleteErrorModalRef.componentInstance.title = subjectModal['delete-error'];
          },
          complete: () => {
            this.getSubjects();
          }
        })
      }
    )
      .catch(
        () => { return; }
      )
  }

  setDefaultRestriction(id: number) {
    const modalRef = this.modalService.open(RestrictionModalComponent, {
      centered: true,
      backdrop: 'static',
      keyboard: false
    });

    this.restrictionService.getDefaultForSubject(id).subscribe({
      next: (dto: DefaultRestrictionDto[]) => {
        if (dto.length > 0) {
          if (dto[0].subjectId == null) {
            modalRef.componentInstance.isInputRestrictionTypeDifferent = true;
            if (dto[0].universityId != null) {
              modalRef.componentInstance.inputRestritionTypeLevel = DefaultRestrictionLevel.UNIVERSITY;
            } else {
              modalRef.componentInstance.inputRestritionTypeLevel = DefaultRestrictionLevel.FUNCTION;
            }
          }
          modalRef.componentInstance.inputValue = new ModalRestrictionDto(dto[0].restrictionType,
            dto[0].attemptsRemaining, dto[0].minutesForAttempt, dto[0].deadline);
        }
      }
    }
    )

    modalRef.componentInstance.title = restrictionModalHeaders.default;

    modalRef.componentInstance.saveAttempt.subscribe(
      (value: ModalRestrictionDto) => {
        let dto = new DefaultRestrictionDto(null, value.restrictionType, null, id, null,
          value.attemptsRemaining, value.minutesForAttempt, value.deadline);
        this.restrictionService.setDefaultRestriction(dto).subscribe({
          complete: () => {
            modalRef.close();
            this.getSubjects();
          }
        })
      }
    );
  }

  setRestriction(id: number) {
    const modalRef = this.modalService.open(RestrictionModalComponent, {
      centered: true,
      backdrop: 'static',
      keyboard: false
    });

    modalRef.componentInstance.title = restrictionModalHeaders['non-default'];

    modalRef.componentInstance.saveAttempt.subscribe(
      (value: ModalRestrictionDto) => {
        let dto = new RestrictionDto(value.restrictionType, null, id, null, null,
          value.attemptsRemaining, value.minutesForAttempt, value.deadline);
        this.restrictionService.setRestriction(dto).subscribe({
          complete: () => {
            modalRef.close();
            this.getSubjects();
          }
        })
      }
    );
  }

  onSearchChange(value: string) {
    this.searchSubject.next(value);
  }

  isAdmin() {
    return this.authService.isAdmin();
  }
}
