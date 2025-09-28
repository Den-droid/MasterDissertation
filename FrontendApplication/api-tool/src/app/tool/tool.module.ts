import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ToolComponent } from './tool.component';
import { toolTitles } from '../shared/translations/tool.translation';

const toolRoutes: Routes = [
  { path: "", component: ToolComponent, title: toolTitles.tool },
  { path: "**", redirectTo: "/error/404" }
]

@NgModule({
  imports: [
    RouterModule.forChild(toolRoutes)
  ],
  exports: [RouterModule],
  bootstrap: []
})
export class ToolModule { }
