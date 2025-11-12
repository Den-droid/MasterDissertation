import { Component, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-modal-info',
  templateUrl: './info-modal.component.html',
})
export class InfoModalComponent {
  @Input() title = '';
  @Input() content = [];

  constructor(public activeModal: NgbActiveModal) { }
}
