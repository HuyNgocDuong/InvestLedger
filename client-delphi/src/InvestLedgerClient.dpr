program InvestLedgerClient;

uses
  Vcl.Forms,
  uMainForm in 'uMainForm.pas' {MainForm},
  uApiClient in 'uApiClient.pas',
  uModels in 'uModels.pas';

{$R *.res}

begin
  Application.Initialize;
  Application.MainFormOnTaskbar := True;
  Application.CreateForm(TMainForm, MainForm);
  Application.Run;
end.
