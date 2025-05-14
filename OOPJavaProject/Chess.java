import java.lang.*;
import java.util.*;


class Board{
    int BlackScore = 0;
    int WhiteScore = 0;
    int Moves = 0;
    boolean BlackIsChecked = false;
    boolean WhiteIsChecked = false;
    Vector<Piece> Pieces = new Vector<Piece>();
    Vector<Piece> Captured = new Vector<Piece>();
    int IsOccupied(int CurX, int CurY){
        for(int i = 0; i<Pieces.size(); i++){
            if(Pieces.get(i).PosX == CurX && Pieces.get(i).PosY == CurY && Pieces.get(i).IsVisible == true){
                if(Pieces.get(i).Color==false){
                    return 1;
                }
                return 2;
            }
        }
        return 0;
    }
    Piece GetPieceAt(int PosX, int PosY){
        for(int i = 0; i<Pieces.size(); i++){
            if(Pieces.get(i).PosX == PosX && Pieces.get(i).PosY == PosY && Pieces.get(i).IsVisible == true){
                return Pieces.get(i);
            }
        }
        return null;
    }  
    void SetBoard(){
        for(int i = 0; i<=7; i++){
            Pawn NewWhitePawn = new Pawn(false, i, 1, this);
            Pawn NewBlackPawn = new Pawn(true, i, 6, this);
            Pieces.add(NewWhitePawn);
            Pieces.add(NewBlackPawn);
        }
        for(int i = 0; i<=2; i++){
            Rook NewWhiteRook = new Rook(false, i*7, 0, this);
            Rook NewBlackRook = new Rook(true, i*7, 7, this);
            Knight NewWhiteKnight = new Knight(false, i*6 +(1-i), 0, this);
            Knight NewBlackKnight = new Knight(true, i*6 +(1-i), 7, this);
            Bishop NewWhiteBishop = new Bishop(false, i*5 +(2-2*i), 0, this);
            Bishop NewBlackBishop = new Bishop(true, i*5 +(2-2*i), 7, this);
            Pieces.add(NewWhiteRook);
            Pieces.add(NewBlackRook);
            Pieces.add(NewWhiteKnight);
            Pieces.add(NewBlackKnight);
            Pieces.add(NewWhiteBishop);
            Pieces.add(NewBlackBishop);
        }
        Queen NewWhiteQueen = new Queen(false, 3, 0, this);
        Queen NewBlackQueen = new Queen(true, 3, 7, this);
        King NewWhiteKing = new King(false, 4, 0, this);
        King NewBlackKing = new King(true, 4, 7, this);
        Pieces.add(NewWhiteQueen);
        Pieces.add(NewBlackQueen);
        Pieces.add(NewWhiteKing);
        Pieces.add(NewBlackKing);
    }
    boolean Move(int OldX, int OldY, int NewX, int NewY){
        Piece CurrentPiece = GetPieceAt(OldX, OldY);
        Piece OccupiedBy = GetPieceAt(NewX, NewY);
        for(int i = 0; i<Pieces.size(); i++){
            int VisibilityCode = 0;
            if(Pieces.get(i).IsVisible == true){
                VisibilityCode = 1;
            }
            if(Pieces.get(i).IsVisible == false){
                VisibilityCode = 0;
            }
            Pieces.get(i).History.add(new int[] {Pieces.get(i).PosX, Pieces.get(i).PosY, VisibilityCode});
        }
        CurrentPiece.PosX = NewX;
        CurrentPiece.PosY = NewY;
        if(OccupiedBy != null){
            if(OccupiedBy.Color != CurrentPiece.Color && OccupiedBy.IsVisible == true){
                OccupiedBy.IsVisible = false;
            }
        }
        if(IsChecked(CurrentPiece.Color) == true){
            Undo();
            return false;
        }
        return true;
    }
    void PrintBoard(Piece SelectedPiece){
        Piece CurrentPiece = null;
        String ColorSymbol;
        System.out.println("Moves:"+Moves);
        System.out.println("Points\nBlack: "+BlackScore+"\tChecked: "+BlackIsChecked+"\nWhite: "+WhiteScore+"\tChecked: "+WhiteIsChecked);
        System.out.print(" \t  A    B    C    D    E    F    G    H\n");
        for(int CurY = 0; CurY<8; CurY++){
            System.out.print((CurY+1)+"\t");
            for(int CurX = 0; CurX<8; CurX++){
                CurrentPiece = GetPieceAt(CurX, CurY);
                if(SelectedPiece == null){
                    if(CurrentPiece == null){
                        System.out.print("  -  ");
                        continue;
                    }
                    if(CurrentPiece.IsVisible == false){
                        System.out.print("  -  ");
                    }
                    else{
                        System.out.print("  "+CurrentPiece.Symbol+"  ");  
                    }
                }
                else{
                    if(CurrentPiece == null){
                        if(SelectedPiece.MoveIsLegal(CurX, CurY) == true){
                            System.out.print("\u001B[42m"+"  -  "+"\u001B[0m");
                        }
                        else{
                            System.out.print("  -  ");
                        }
                        
                        continue;
                    }
                    if(CurrentPiece.IsVisible == false){
                        if(SelectedPiece.MoveIsLegal(CurX, CurY) == true){
                            System.out.print("\u001B[42m"+"  -  "+"\u001B[0m");
                        }
                        else{
                            System.out.print("  -  ");
                        }
                    }
                    else{
                        if(SelectedPiece.PosX == CurrentPiece.PosX && SelectedPiece.PosY == CurrentPiece.PosY){
                            System.out.print("\u001B[41m"+"  "+CurrentPiece.Symbol+"  "+"\u001B[0m");
                        }
                        else if(SelectedPiece.MoveIsLegal(CurX, CurY) == true){
                            System.out.print("\u001B[42m"+"  "+CurrentPiece.Symbol+"  "+"\u001B[0m");
                        }
                        else{
                            System.out.print("  "+CurrentPiece.Symbol+"  ");  
                        }
                        
                    }
                }
               
            }
            System.out.print("\t"+(CurY+1)+"\n\n");
        }
        System.out.print(" \t  A    B    C    D    E    F    G    H\n");
    }
    boolean IsChecked(boolean KingColor){
        Piece CheckKing = null;
        for(int i = 0; i<Pieces.size(); i++){
            if(Pieces.get(i) instanceof King && Pieces.get(i).Color == KingColor){
                CheckKing = Pieces.get(i);
                break;
            }
        }
        for(int i = 0; i<Pieces.size(); i++){
            if(Pieces.get(i).Color != KingColor && Pieces.get(i).IsVisible ==  true){
                if(Pieces.get(i).MoveIsLegal(CheckKing.PosX, CheckKing.PosY) == true){
                    return true;
                }
            }
        }
        return false;
    }
    boolean IsCheckMate(boolean KingColor){
        Piece CheckKing = null;
        for(int i = 0; i<Pieces.size(); i++){
            if(Pieces.get(i) instanceof King && Pieces.get(i).Color == KingColor){
                CheckKing = Pieces.get(i);
                break;
            }
        }
        for(int i = 0; i<Pieces.size(); i++){
            if(Pieces.get(i).Color != KingColor && Pieces.get(i).IsVisible == true){
                for(int CurY = 0; CurY<8; CurY++){
                    for(int CurX = 0; CurX<8; CurX++){
                        if(Pieces.get(i).MoveIsLegal(CurX, CurY)){
                            Move(Pieces.get(i).PosX, Pieces.get(i).PosY, CurX, CurY);
                            if(IsChecked(KingColor) == true){
                                return false;
                            }
                            Undo();
                        }
                    }
                }
            }
        }
        return true;
    }
    void Undo(){
        try{
            for(int i = 0; i<Pieces.size(); i++){
                Pieces.get(i).PosX = Pieces.get(i).History.get(Pieces.get(i).History.size()-1)[0];
                Pieces.get(i).PosY = Pieces.get(i).History.get(Pieces.get(i).History.size()-1)[1];
                boolean VisibilityBool = false;
                if(Pieces.get(i).History.get(Pieces.get(i).History.size()-1)[2]==1){
                    VisibilityBool = true;
                }
                Pieces.get(i).IsVisible = VisibilityBool;
                Pieces.get(i).History.remove(Pieces.get(i).History.size()-1);
            }
            Moves--;
        }
        catch(ArrayIndexOutOfBoundsException e){
            
        }
        
    }
}

abstract class Piece{
    abstract boolean MoveIsLegal(int NewX, int NewY);
    Vector<int[]> History = new Vector<int[]>();
    boolean IsVisible = true;
    Board CurrentBoard;
    int PosX, PosY, Weight;
    String Symbol = null;
    //Color represents piece color. True is black. Black starts at bottom (index 7)
    boolean Color;
    Piece(boolean Color_in, int PosX_in, int PosY_in, String BlackSymbol_in, String WhiteSymbol_in, Board CurrentBoard_in, int Weight_in){
        PosX = PosX_in;
        PosY = PosY_in;
        Color = Color_in;
        CurrentBoard = CurrentBoard_in;
        Weight = Weight_in;
        if(Color == true){
            Symbol = BlackSymbol_in;
        }else{
            Symbol = WhiteSymbol_in;
        }
    }
}
class Pawn extends Piece{
    Pawn(boolean Color_in, int PosX_in, int PosY_in, Board CurrentBoard_in){
        super(Color_in, PosX_in, PosY_in, "♟️", "♙", CurrentBoard_in, 1);
    }
    boolean MoveIsLegal(int NewX, int NewY){
        if(NewX>7||NewX<0||NewY>7||NewY<0){
            return false;
        }
        if(CurrentBoard.GetPieceAt(NewX, NewY) != null){
            if(CurrentBoard.GetPieceAt(NewX, NewY).Color == Color){
                return false;
            }
        }
        if(Color == false){
            if(PosY == 1 && PosX == NewX && NewY == 3){
                if(CurrentBoard.GetPieceAt(NewX, NewY) != null){
                    return false;
                }
                return true;
            }
            if(NewY == PosY+1 && NewX == PosX){
                return true;
            }
            if(NewY == PosY+1 && (NewX == PosX+1 || NewX == PosX-1)){
                if(CurrentBoard.GetPieceAt(NewX, NewY) != null){
                    if(CurrentBoard.GetPieceAt(NewX, NewY).Color != Color){
                        return true;
                    }
                }
                return false;
            }
        }
        else{
            if(PosY == 6 && PosX == NewX && NewY == 4){
                if(CurrentBoard.GetPieceAt(NewX, NewY) != null){
                    return false;
                }
                return true;
            }
            if(NewY == PosY-1 && NewX == PosX){
                return true;
            }
            if(NewY == PosY-1 && (NewX == PosX+1 || NewX == PosX-1)){
                if(CurrentBoard.GetPieceAt(NewX, NewY) != null){
                    if(CurrentBoard.GetPieceAt(NewX, NewY).Color != Color){
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }
}
class Rook extends Piece{
    Rook(boolean Color_in, int PosX_in, int PosY_in, Board CurrentBoard_in){
        super(Color_in, PosX_in, PosY_in, "♜", "♖", CurrentBoard_in, 5);
    }
    boolean MoveIsLegal(int NewX, int NewY){
        if(NewX>7||NewX<0||NewY>7||NewY<0){
            return false;
        }
        if(CurrentBoard.GetPieceAt(NewX, NewY) != null){
            if(CurrentBoard.GetPieceAt(NewX, NewY).Color == Color){
                return false;
            }
        }
        if(NewX == PosX && NewY != PosY){
            int MaxY = Math.max(NewY, PosY);
            int MinY = Math.min(NewY, PosY);
            for(int i = MinY+1; i<MaxY; i++){
                if(CurrentBoard.GetPieceAt(PosX, MinY+i) != null){
                    return false;
                }
            }
            return true;
        }
        if(NewX != PosX && NewY == PosY){
            int MaxX = Math.max(NewX, PosX);
            int MinX = Math.min(NewX, PosX);
            for(int i = MinX+1; i<MaxX; i++){
                if(CurrentBoard.GetPieceAt(MinX+i, PosY) != null){
                    return false;
                }
            }
            return true;
        }
        else{
            return false;
        }
    }
}

class Knight extends Piece{
    Knight(boolean Color_in, int PosX_in, int PosY_in, Board CurrentBoard_in){
        super(Color_in, PosX_in, PosY_in, "♞", "♘", CurrentBoard_in, 3);
    }
    boolean MoveIsLegal(int NewX, int NewY){
        if(NewX>7||NewX<0||NewY>7||NewY<0){
            return false;
        }
        if(CurrentBoard.GetPieceAt(NewX, NewY) != null){
            if(CurrentBoard.GetPieceAt(NewX, NewY).Color == Color){
                return false;
            }
        }
        if(((NewX == PosX+1 || NewX == PosX-1) && (NewY==PosY+2||NewY==PosY-2)) || ((NewX == PosX+2 || NewX == PosX-2) && (NewY==PosY+1||NewY==PosY-1))){
            return true;
        }
        return false;
    }
}

class Bishop extends Piece{
    Bishop(boolean Color_in, int PosX_in, int PosY_in, Board CurrentBoard_in){
        super(Color_in, PosX_in, PosY_in, "♝", "♗", CurrentBoard_in, 3);
    }
    boolean MoveIsLegal(int NewX, int NewY){
        if(NewX>7||NewX<0||NewY>7||NewY<0){
            return false;
        }
        if(CurrentBoard.GetPieceAt(NewX, NewY) != null){
            if(CurrentBoard.GetPieceAt(NewX, NewY).Color == Color){
                return false;
            }
        }
        if(Math.abs(NewX-PosX)==Math.abs(NewY-PosY)){
            if((NewX<PosX && NewY<PosY)||(NewX>PosX && NewY>PosY)){
                int MinX = Math.min(NewX, PosX);
                int MaxX = Math.max(NewX, PosX);
                for(int i = MinX+1; i<MaxX; i++){
                    if(CurrentBoard.GetPieceAt(i, PosY-MinX+i)  != null){
                        return false;
                    }
                }
                if(CurrentBoard.GetPieceAt(NewX, NewY) == null){
                    return true;
                }
                else{
                    if(CurrentBoard.GetPieceAt(NewX, NewY).Color != Color){
                        return true;
                    }else{
                        return false;
                    }
                }
            }
            if((NewX>PosX && NewY<PosY)||(NewX<PosX && NewY>PosY)){
                int MinX = Math.min(NewX, PosX);
                int MaxX = Math.max(NewX, PosX);
                for(int i = MinX+1; i<MaxX; i++){
                    if(CurrentBoard.GetPieceAt(i, PosY+MaxX-i)  != null){
                        return false;
                    }
                }
                if(CurrentBoard.GetPieceAt(NewX, NewY) == null){
                    return true;
                }
                else{
                    if(CurrentBoard.GetPieceAt(NewX, NewY).Color != Color){
                        return true;
                    }else{
                        return false;
                    }
                }
            }
        }
        
        return false;
    }
}
class Queen extends Piece{
    Queen(boolean Color_in, int PosX_in, int PosY_in, Board CurrentBoard_in){
        super(Color_in, PosX_in, PosY_in, "♛", "♕", CurrentBoard_in, 3);
    }
    boolean MoveIsLegal(int NewX, int NewY){
        if(NewX>7||NewX<0||NewY>7||NewY<0){
            return false;
        }
        if(CurrentBoard.GetPieceAt(NewX, NewY) != null){
            if(CurrentBoard.GetPieceAt(NewX, NewY).Color == Color){
                return false;
            }
        }
        if(NewX == PosX && NewY != PosY){
            int MaxY = Math.max(NewY, PosY);
            int MinY = Math.min(NewY, PosY);
            for(int i = MinY+1; i<MaxY; i++){
                if(CurrentBoard.GetPieceAt(PosX, i) != null){
                    return false;
                }
            }
            return true;
        }
        if(NewX != PosX && NewY == PosY){
            int MaxX = Math.max(NewX, PosX);
            int MinX = Math.min(NewX, PosX);
            for(int i = MinX+1; i<MaxX; i++){
                if(CurrentBoard.GetPieceAt(i, PosY) != null){
                    return false;
                }
            }
            return true;
        }

        if(Math.abs(NewX-PosX)==Math.abs(NewY-PosY)){
            if((NewX<PosX && NewY<PosY)||(NewX>PosX && NewY>PosY)){
                int MinX = Math.min(NewX, PosX);
                int MaxX = Math.max(NewX, PosX);
                for(int i = MinX+1; i<MaxX; i++){
                    if(CurrentBoard.GetPieceAt(i, PosY-MinX+i)  != null){
                        return false;
                    }
                }
                if(CurrentBoard.GetPieceAt(NewX, NewY) == null){
                    return true;
                }
                else{
                    if(CurrentBoard.GetPieceAt(NewX, NewY).Color != Color){
                        return true;
                    }else{
                        return false;
                    }
                }
            }
            if((NewX>PosX && NewY<PosY)||(NewX<PosX && NewY>PosY)){
                int MinX = Math.min(NewX, PosX);
                int MaxX = Math.max(NewX, PosX);
                for(int i = MinX+1; i<MaxX; i++){
                    if(CurrentBoard.GetPieceAt(i, PosY+MaxX-i)  != null){
                        return false;
                    }
                }
                if(CurrentBoard.GetPieceAt(NewX, NewY) == null){
                    return true;
                }
                else{
                    if(CurrentBoard.GetPieceAt(NewX, NewY).Color != Color){
                        return true;
                    }else{
                        return false;
                    }
                }
            }
        }
        return false;
    }
}
class King extends Piece{
    King(boolean Color_in, int PosX_in, int PosY_in, Board CurrentBoard_in){
        super(Color_in, PosX_in, PosY_in, "♚", "♔", CurrentBoard_in, 3);
    }
    boolean MoveIsLegal(int NewX, int NewY){
        if(NewX>7||NewX<0||NewY>7||NewY<0){
            return false;
        }
        if((NewX == PosX+1||NewX == PosX-1)&&(NewY == PosY+1 || NewY == PosY-1)||((NewX == PosX+1||NewX == PosX-1)&&(NewY==PosY))||((NewY == PosY+1||NewY == PosY-1)&&(NewX==PosX))){
            return true;
        }
        return false;
    }
}

class Chess{
    public static void ClrScr() {  
        System.out.print("\033[H\033[2J");  
        System.out.flush();  
    }  

    public static void main(String Args[]){
        Board CurrentBoard = new Board();
        CurrentBoard.SetBoard();
        CurrentBoard.PrintBoard(null);
        System.out.println(CurrentBoard.Pieces.get(2).MoveIsLegal(1,5)+"");
        System.out.println(CurrentBoard.Pieces.get(2).MoveIsLegal(1,2)+"");
        Scanner S  = new Scanner(System.in);
        boolean Color = false;
        boolean ValidPieceSelected = false;
        boolean ValidMoveSelected = false;
        boolean ValidPosSelected = false;
        Piece PieceAt = null;
        int Row = 0;
        int Column = 0;
        int NewRow = 0;
        int NewColumn = 0;
        String Square = null;
        String  Choice = null;
        boolean Running = true;
        while(Running){
            ClrScr();
            CurrentBoard.PrintBoard(null);
            ValidPieceSelected = false;
            ValidMoveSelected = false;
            if(Color == true){
                System.out.println("Black's turn to move");
            }
            if(Color == false){
                System.out.println("White's turn to move");
            }
            System.out.println("Choose piece to move");
            System.out.print("Square:");
            Square = S.next();
            Row = Square.charAt(0)-'a';
            Column = Square.charAt(1)-'0'-1;

            PieceAt = CurrentBoard.GetPieceAt(Row, Column);
            if(PieceAt != null){
                if(CurrentBoard.GetPieceAt(Row, Column).Color == Color){

                    ValidPieceSelected = true;
                    ClrScr();
                        CurrentBoard.PrintBoard(CurrentBoard.GetPieceAt(Row, Column));
                }else{
                    System.out.println("Please choose a valid piece");
                }
                
            }else{
                System.out.println("Please choose a valid piece");
            }
            while(ValidPieceSelected == false){
                System.out.println("Choose piece to move");
                System.out.print("Square:");
                Square = S.next();
                Row = Square.charAt(0)-'a';
                Column = Square.charAt(1)-'0'-1;

                PieceAt = CurrentBoard.GetPieceAt(Row, Column);
                if(PieceAt != null){
                    if(CurrentBoard.GetPieceAt(Row, Column).Color == Color){
                        ValidPieceSelected = true;
                        ClrScr();
                        CurrentBoard.PrintBoard(CurrentBoard.GetPieceAt(Row, Column));
                        break;
                    }
                    System.out.println("Please choose a valid piece");
                }else{
                    System.out.println("Please choose a valid piece");
                }
            }
            System.out.println("Selected:"+PieceAt.Symbol+" at "+PieceAt.PosX+","+PieceAt.PosY);
            while(ValidMoveSelected == false){
                System.out.println("Choose piece to move piece to");
                System.out.print("Square:");
                Square = S.next();
                NewRow = Square.charAt(0)-'a';
                NewColumn = Square.charAt(1)-'0'-1;
                if(PieceAt.MoveIsLegal(NewRow, NewColumn)){
                        if(CurrentBoard.Move(Row, Column, NewRow, NewColumn)==false){
                            System.out.println("Move causes check!");
                            ValidMoveSelected = false;
                            continue;
                        }
                        ValidMoveSelected = true;
                        ClrScr();
                        CurrentBoard.PrintBoard(CurrentBoard.GetPieceAt(Row, Column));
                        continue;
                }
                else{
                    System.out.println("Please choose a valid move");
                }
            }
            CurrentBoard.WhiteIsChecked = false;
            CurrentBoard.BlackIsChecked = false;
            Color = !Color;
            if(CurrentBoard.IsChecked(false) == true){
                CurrentBoard.WhiteIsChecked = true;
                if(CurrentBoard.IsCheckMate(false) == true){
                    System.out.println("Black won by checkmate!");
                    return;
                }
            }
            if(CurrentBoard.IsChecked(true) == true){
                CurrentBoard.BlackIsChecked = true;
                if(CurrentBoard.IsCheckMate(true) == true){
                    System.out.println("White won by checkmate!");
                    return;
                }
            }
        }

        return;
    }

}

