// 여긴 클라이언트에게 무엇을 보여줄 것인가를 생각하는 곳
// Entity는 DB와 1:1 이기에 직접 가져다 써도 되지만 
// Dto에서 한번 더 감아서 사용. ex) BoardListDto 
// 데이터 흐름 : 사용자 -> DTO -> Controller -> Service -> Entity -> Repository -> DB
// Dto -> data를 담는 상자 , Repository -> DB에 넣고 꺼내는 담당자.