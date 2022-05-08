unit Unit1;

{$mode objfpc}{$H+}

interface

uses
  Classes, SysUtils, Forms, Controls, Graphics, Dialogs, StdCtrls,
  websocket2,
  //customserver2,
  Unit2;

type

  { TTestWebSocketClientConnection }

  {TTestWebSocketClientConnection = class(TWebSocketClientConnection)
    fFramedText: string;
    fFramedStream: TMemoryStream;

    fPing: string;
    fPong: string;

    procedure ProcessText(aFinal, aRes1, aRes2, aRes3: boolean; aData: string); override;
    procedure ProcessTextContinuation(aFinal, aRes1, aRes2, aRes3: boolean; aData: string); override;

    procedure ProcessStream(aFinal, aRes1, aRes2, aRes3: boolean; aData: TMemoryStream); override;
    procedure ProcessStreamContinuation(aFinal, aRes1, aRes2, aRes3: boolean; aData: TMemoryStream); override;

    procedure ProcessPing(aData: string); override;
    procedure ProcessPong(aData: string); override;


    procedure SyncTextFrame;
    procedure SyncBinFrame;

    procedure SyncPing;
    procedure SyncPong;

  public
    constructor Create(aHost, aPort, aResourceName: string;
    aOrigin: string = '-'; aProtocol: string = '-'; aExtension: string = '-';
    aCookie: string = '-'; aVersion: integer = 8); override;
    destructor Destroy; override;
    property ReadFinal: boolean read fReadFinal;
    property ReadRes1: boolean read fReadRes1;
    property ReadRes2: boolean read fReadRes2;
    property ReadRes3: boolean read fReadRes3;
    property ReadCode: integer read fReadCode;
    property ReadStream: TMemoryStream read fReadStream;

    property WriteFinal: boolean read fWriteFinal;
    property WriteRes1: boolean read fWriteRes1;
    property WriteRes2: boolean read fWriteRes2;
    property WriteRes3: boolean read fWriteRes3;
    property WriteCode: integer read fWriteCode;
    property WriteStream: TMemoryStream read fWriteStream;

  end;
  }



  { TForm1 }

  TForm1 = class(TForm)
    Button1: TButton;
    Button2: TButton;
    SERVERIP: TEdit;
    SERVERPORT: TEdit;
    LOGINID: TEdit;
    procedure Button1Click(Sender: TObject);
    procedure Button2Click(Sender: TObject);
  private
    {fClient: TWebSocketClientConnection;
    fFrameString: string;

    procedure OnOpen(aSender: TWebSocketCustomConnection);
    procedure OnRead(aSender: TWebSocketCustomConnection; aFinal, aRes1, aRes2, aRes3: boolean; aCode: integer; aData: TMemoryStream);
    procedure OnWrite(aSender: TWebSocketCustomConnection; aFinal, aRes1, aRes2, aRes3: boolean; aCode: integer; aData: TMemoryStream);
    procedure OnClose(aSender: TWebSocketCustomConnection; aCloseCode: integer; aCloseReason: string; aClosedByPeer: boolean);
    //procedure OnConnectionSocket(Sender: TObject; Reason: THookSocketReason; const Value: String);
    }
  public

  end;

var
  Form1: TForm1;

implementation

{$R *.lfm}

{ TForm1 }

procedure TForm1.Button1Click(Sender: TObject);
var
  serverip_s, serverport_s, loginid_s: String;
begin
     serverip_s := SERVERIP.Text;
     serverport_s := SERVERPORT.Text;
     loginid_s := LOGINID.Text;

     Form2 := TForm2.Create(nil);
     try
       Form2.ShowModal;
     finally
       Form2.Free;
     end;

end;

procedure TForm1.Button2Click(Sender: TObject);
begin
{  fClient := TTestWebSocketClientConnection.Create(SERVERIP.Text, SERVERPORT.Text, '/');

  //fClient.OnRead := OnRead;
  //fClient.OnWrite := OnWrite;
  //fClient.OnClose := OnClose;
  //fClient.OnOpen := OnOpen;
  //fClient.Socket.OnSyncStatus := OnConnectionSocket;

  fClient.SSL := boolean(false);
  fClient.Start;
}
end;

{procedure TForm1.OnOpen(aSender: TWebSocketCustomConnection);
begin
{  InfoMemo.Lines.Insert(0, Format('OnOpen %d', [aSender.Index]));
  StartButton.Enabled := false;
}
end;

procedure TForm1.OnRead(aSender: TWebSocketCustomConnection; aFinal, aRes1, aRes2, aRes3: boolean; aCode: integer; aData: TMemoryStream);
var s: string;
    c: TTestWebSocketClientConnection;
begin
{  c := TTestWebSocketClientConnection(aSender);
  InfoMemo.Lines.Insert(0, Format('OnRead %d, final: %d, ext1: %d, ext2: %d, ext3: %d, type: %d, length: %d', [aSender.Index, ord(aFinal), ord(aRes1), ord(aRes2), ord(aRes3), aCode, aData.Size]));
  s := ReadStrFromStream(c.ReadStream, min(c.ReadStream.size, 10 * 1024));
  if (c.ReadCode = wsCodeText) then
    LastReceivedMemo.Lines.text := CharsetConversion(s, UTF_8, GetCurCP)
  else
    LastReceivedMemo.Lines.text := s;
}
end;

procedure TForm1.OnWrite(aSender: TWebSocketCustomConnection; aFinal, aRes1, aRes2, aRes3: boolean; aCode: integer; aData: TMemoryStream);
var s: string;
    c: TTestWebSocketClientConnection;
begin
{  c := TTestWebSocketClientConnection(aSender);
  InfoMemo.Lines.Insert(0, Format('OnWrite %d, final: %d, ext1: %d, ext2: %d, ext3: %d, type: %d, length: %d', [aSender.Index, ord(aFinal), ord(aRes1), ord(aRes2), ord(aRes3), aCode, aData.Size]));

  s := ReadStrFromStream(c.WriteStream, min(c.WriteStream.size, 10 * 1024));
  if (c.ReadCode = wsCodeText) then
    LastSentMemo.Lines.text := CharsetConversion(s, UTF_8, GetCurCP)
  else
    LastSentMemo.Lines.text := s;
}
end;

procedure TForm1.OnClose(aSender: TWebSocketCustomConnection;
  aCloseCode: integer; aCloseReason: string; aClosedByPeer: boolean);
begin
{  InfoMemo.Lines.Insert(0, Format('OnClose %d, %d, %s, %s', [aSender.Index, aCloseCode, aCloseReason, IfThen(aClosedByPeer, 'closed by peer', 'closed by me')]));
  EndButton.Enabled := false;
  StartButton.Enabled := true;
}
end;

//procedure TForm1.OnConnectionSocket(Sender: TObject;
//  Reason: THookSocketReason; const Value: String);
//begin
{  InfoMemo.Lines.Insert(0, Format('OnConnectionSocket %d, %s, %s', [
    TTCPCustomConnectionSocket(Sender).Connection.Index,
    GetEnumName(TypeInfo(THookSocketReason), ord(Reason)),
    value
  ]));
}
//end;


{ TTestWebSocketClientConnection }

procedure TTestWebSocketClientConnection.ProcessText(aFinal, aRes1, aRes2,
  aRes3: boolean; aData: string);
begin
//  fFramedText := aData;
end;

procedure TTestWebSocketClientConnection.ProcessTextContinuation(aFinal, aRes1,
  aRes2, aRes3: boolean; aData: string);
begin
//  fFramedText := fFramedText + aData;
//  if (aFinal) then
//  begin
//    Synchronize(SyncTextFrame);
//  end;
end;

procedure TTestWebSocketClientConnection.ProcessStream(aFinal, aRes1, aRes2,
  aRes3: boolean; aData: TMemoryStream);
begin
//  fFramedStream.Size := 0;
//  fFramedStream.CopyFrom(aData, aData.Size);
//  MainForm.InfoMemo.Lines.Insert(0, Format('ProcessStream %d, %d, %d, %d, %d, %d ', [Index, ord(aFinal), ord(aRes1), ord(aRes2), ord(aRes3), aData.Size]));
//  if (aFinal) then
//  begin
//    Synchronize(SyncBinFrame);
//  end;
end;

procedure TTestWebSocketClientConnection.ProcessStreamContinuation(aFinal,
  aRes1, aRes2, aRes3: boolean; aData: TMemoryStream);
begin
//  fFramedStream.CopyFrom(aData, aData.Size);
//  MainForm.InfoMemo.Lines.Insert(0, Format('ProcessStreamContinuation %d, %d, %d, %d, %d, %d ', [Index, ord(aFinal), ord(aRes1), ord(aRes2), ord(aRes3), aData.Size]));
//  if (aFinal) then
//  begin
//    Synchronize(SyncBinFrame);
//  end;
end;

procedure TTestWebSocketClientConnection.ProcessPing(aData: string);
begin
//  Pong(aData);
//  fPing := aData;
//  Synchronize(SyncPing);
end;

procedure TTestWebSocketClientConnection.ProcessPong(aData: string);
begin
//  fPong := aData;
//  Synchronize(SyncPong);
end;

procedure TTestWebSocketClientConnection.SyncTextFrame;
begin
//  MainForm.FrameReceiveMemo.Text :=  CharsetConversion(fFramedText, UTF_8, GetCurCP);
end;

procedure TTestWebSocketClientConnection.SyncBinFrame;
var png : TPortableNetworkGraphic;
begin
//  MainForm.InfoMemo.Lines.Insert(0, Format('SyncBinFrame %d', [fFramedStream.Size]));
//  png := TPortableNetworkGraphic.Create;
//  fFramedStream.Position := 0;
//  png.LoadFromStream(fFramedStream);
//  MainForm.Image1.Picture.Assign(png);
//  png.Free;
end;

procedure TTestWebSocketClientConnection.SyncPing;
begin
//  Form1.InfoMemo.Lines.Insert(0, Format('SyncPing %s', [fPing]));
end;

procedure TTestWebSocketClientConnection.SyncPong;
begin
//  Form1.InfoMemo.Lines.Insert(0, Format('SyncPong %s', [fPong]));
end;

constructor TTestWebSocketClientConnection.Create(aHost, aPort, aResourceName: string;
    aOrigin: string = '-'; aProtocol: string = '-'; aExtension: string = '-';
    aCookie: string = '-'; aVersion: integer = 8);
begin
  inherited;
//  fFramedText := '';
  fFramedStream := TMemoryStream.Create;
end;

destructor TTestWebSocketClientConnection.Destroy;
begin
  fFramedStream.free;
  inherited;
end;
}

end.

