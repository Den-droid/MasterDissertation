export class SignInDto {
  constructor(public apiKey : string) {
  }
}

export class TokensDto {
  constructor(public accessToken: string, public refreshToken: string) {
  }
}

export class RefreshTokenDto {
  constructor(public refreshToken: string) { }
}
