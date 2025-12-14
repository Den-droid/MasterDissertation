export class MazePointDto {
    public constructor(public x: number, public y: number) {

    }
}

export class AddMazeDto {
    public constructor(public name: string, public width: number, public height: number,
        public universityId: number, public startPoint: MazePointDto, public endPoint: MazePointDto,
        public walls: MazePointDto[]) {

    }
}