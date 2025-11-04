import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FunctionListComponent } from './function-list/function-list.component';
import { functionPageTitles } from '../shared/translations/function.translation';

const functionRoutes: Routes = [
  { path: "", component: FunctionListComponent, title: functionPageTitles.functions }
]

@NgModule({
  imports: [
    RouterModule.forChild(functionRoutes)
  ],
  exports: [RouterModule],
  bootstrap: []
})
export class FunctionModule { }
