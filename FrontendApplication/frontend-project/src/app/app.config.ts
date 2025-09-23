import { ApplicationConfig, provideBrowserGlobalErrorListeners, provideZoneChangeDetection } from '@angular/core';
import { provideRouter, Routes } from '@angular/router';

import { AuthComponent } from './auth/auth.component';
import { ErrorComponent } from './error/error.component';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { StudentComponent } from './student/student.component';
import { AssignmentsComponent } from './assignments/assignments.component';
import { authorizeInterceptor } from './shared/interceptors/authorize.interceptor';

const routes: Routes = [
  { path: "", redirectTo: "/auth/signin", pathMatch: "full" },
  {
    path: "auth", component: AuthComponent,
    loadChildren: () => import('./auth/auth.module').then(m => m.AuthModule)
  },
  {
    path: "error", component: ErrorComponent,
    loadChildren: () => import('./error/error.module').then(m => m.ErrorModule)
  },
  {
    path: "student", component: StudentComponent,
    loadChildren: () => import('./student/student.module').then(m => m.StudentModule)
  },
  {
    path: "assignments", component: AssignmentsComponent,
    loadChildren: () => import('./assignments/assignments.module').then(m => m.AssignmentsModule)
  },
  { path: '**', redirectTo: '/error/404', pathMatch: 'full' }
]

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(withInterceptors([authorizeInterceptor])),
  ]
};
