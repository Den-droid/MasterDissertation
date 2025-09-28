import { CommonModule, Location } from "@angular/common";
import { Component } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { ApiKeyDto } from "../../shared/models/user.model";
import { JWTTokenService } from "../../shared/services/jwt-token.service";
import { UserService } from "../../shared/services/user.service";

@Component({
  selector: 'app-user-apikey',
  templateUrl: './apikey.component.html',
  imports: [FormsModule, CommonModule]
})
export class ApikeyComponent {
  apiKey: string = '';
  copyButtonText = 'Копіювати ключ';

  public constructor(private location: Location, private userService: UserService,
    private jwtService: JWTTokenService) { }

  getNewKey() {
    this.userService.getApiKey(Number(this.jwtService.getId())).subscribe({
      next: (apiKeyDto: ApiKeyDto) => {
        this.apiKey = apiKeyDto.apiKey;
      }
    })
  }

  goBack() {
    this.location.back();
  }

  copyKey() {
    navigator.clipboard.writeText(this.apiKey).then(() => {
      this.copyButtonText = "Ключ скопійовано";
      setTimeout(() => {
        this.copyButtonText = "Копіювати ключ"
      }, 5000);
    });
  }
}
