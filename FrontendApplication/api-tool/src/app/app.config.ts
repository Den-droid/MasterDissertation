import { ApplicationConfig, provideBrowserGlobalErrorListeners, provideZoneChangeDetection } from '@angular/core';
import { provideRouter, Routes } from '@angular/router';

import { AuthComponent } from './auth/auth.component';
import { ErrorComponent } from './error/error.component';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { authorizeInterceptor } from './shared/interceptors/authorize.interceptor';
import { ToolComponent } from './tool/tool.component';
import { authenticatedGuard } from './shared/guards/authenticated.guard';

const routes: Routes = [
  { path: "", redirectTo: "/apitool/tool", pathMatch: "full" },
  {
    path: "apitool/auth", component: AuthComponent,
    loadChildren: () => import('./auth/auth.module').then(m => m.AuthModule)
  },
  {
    path: "apitool/tool", component: ToolComponent,
    loadChildren: () => import('./tool/tool.module').then(m => m.ToolModule),
    canActivate: [authenticatedGuard]
  },
  {
    path: "apitool/error", component: ErrorComponent,
    loadChildren: () => import('./error/error.module').then(m => m.ErrorModule)
  },
  { path: '**', redirectTo: 'apitool/error/404', pathMatch: 'full' }
]

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(withInterceptors([authorizeInterceptor])),
  ]
};
